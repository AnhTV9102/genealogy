package com.genealogy.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genealogy.auth.application.dto.AuthResponse;
import com.genealogy.auth.application.service.AuthService;
import com.genealogy.security.CustomUserDetailsService;
import com.genealogy.security.JwtAuthenticationFilter;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;
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
  void register_returnsTokens() throws Exception {
    AuthResponse response =
        new AuthResponse("access", "refresh", "Bearer", 1000L, Set.of("ROLE_MEMBER"));
    when(authService.register(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        java.util.Map.of(
                            "username", "member1",
                            "email", "member1@example.com",
                            "password", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access"))
        .andExpect(jsonPath("$.refreshToken").value("refresh"));
  }

  @Test
  void login_returnsTokens() throws Exception {
    AuthResponse response =
        new AuthResponse("access", "refresh", "Bearer", 1000L, Set.of("ROLE_MEMBER"));
    when(authService.login(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        java.util.Map.of("username", "member1", "password", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void logout_returnsNoContent() throws Exception {
    doNothing().when(authService).logout(any());

    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("refreshToken", "r1"))))
        .andExpect(status().isNoContent());
  }

  @Test
  void refresh_returnsTokens() throws Exception {
    AuthResponse response =
        new AuthResponse("newAccess", "newRefresh", "Bearer", 1000L, Set.of("ROLE_MEMBER"));
    when(authService.refresh(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("refreshToken", "r1"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("newAccess"));
  }
}
