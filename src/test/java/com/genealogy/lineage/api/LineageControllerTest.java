package com.genealogy.lineage.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genealogy.lineage.application.dto.LineageResponse;
import com.genealogy.lineage.application.service.LineageAccessService;
import com.genealogy.lineage.application.service.LineageService;
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

@WebMvcTest(controllers = LineageController.class)
@Import(SecurityConfig.class)
class LineageControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private LineageService lineageService;
  @MockBean private LineageAccessService lineageAccessService;
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
  @WithMockUser(username = "owner1", roles = {"MEMBER"})
  void authenticatedUser_canCreateLineage() throws Exception {
    when(lineageService.createLineage("PhamFamily", "owner1"))
        .thenReturn(new LineageResponse(1L, "PhamFamily", "owner1"));
    mockMvc
        .perform(
            post("/api/v1/lineages")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(java.util.Map.of("name", "PhamFamily"))))
        .andExpect(status().isOk());
  }
}
