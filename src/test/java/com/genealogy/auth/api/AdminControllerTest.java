package com.genealogy.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genealogy.auth.application.service.AdminUserService;
import com.genealogy.security.CustomUserDetailsService;
import com.genealogy.security.JwtAuthenticationFilter;
import com.genealogy.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

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
  @WithMockUser(roles = {"ADMIN"})
  void admin_canCreateUser() throws Exception {
    doNothing().when(adminUserService).createUser(any());
    mockMvc
        .perform(
            post("/api/v1/admin/users")
                .with(csrf())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        java.util.Map.of(
                            "username",
                            "user1",
                            "email",
                            "u1@example.com",
                            "password",
                            "password123"))))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = {"MEMBER"})
  void nonAdmin_cannotDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/v1/admin/users/2").with(csrf())).andExpect(status().isForbidden());
  }
}
