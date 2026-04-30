package com.genealogy.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genealogy.auth.api.AdminController;
import com.genealogy.auth.application.service.AdminUserService;
import com.genealogy.person.api.PersonController;
import com.genealogy.person.application.service.PersonService;
import com.genealogy.relationship.application.service.RelationshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {PersonController.class, AdminController.class})
@Import(SecurityConfig.class)
class AuthorizationPolicyTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PersonService personService;
  @MockBean private RelationshipService relationshipService;
  @MockBean private AdminUserService adminUserService;
  @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
  @MockBean private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  void setUp() throws Exception {
    doAnswer(
            invocation -> {
              jakarta.servlet.FilterChain chain = invocation.getArgument(2);
              chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
              return null;
            })
        .when(jwtAuthenticationFilter)
        .doFilter(any(), any(), any());
  }

  @Test
  void unauthenticatedRequest_isUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/persons")).andExpect(status().is4xxClientError());
  }

  @Test
  @WithMockUser(authorities = {"PERSON_READ"})
  void readPermission_canAccessPersonsList() throws Exception {
    when(personService.listAll(any())).thenReturn(Page.empty());
    mockMvc.perform(get("/api/v1/persons")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"PERSON_READ"})
  void missingDeletePermission_isForbidden() throws Exception {
    mockMvc.perform(delete("/api/v1/persons/1").with(csrf())).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"PERSON_DELETE"})
  void deletePermission_canDeletePerson() throws Exception {
    doNothing().when(personService).deleteById(1L);
    mockMvc.perform(delete("/api/v1/persons/1").with(csrf())).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  void adminRole_canAccessAdminEndpoint() throws Exception {
    doNothing().when(adminUserService).createUser(any());
    mockMvc
        .perform(
            post("/api/v1/admin/users")
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"username\":\"member2\",\"email\":\"member2@example.com\",\"password\":\"password123\"}"))
        .andExpect(status().isNoContent());
  }
}
