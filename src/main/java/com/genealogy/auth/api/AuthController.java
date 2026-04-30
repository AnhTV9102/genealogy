package com.genealogy.auth.api;

import com.genealogy.auth.application.dto.*;
import com.genealogy.auth.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
    return authService.refresh(request);
  }
}
