package com.genealogy.auth.application.service;

import com.genealogy.auth.application.dto.*;
import com.genealogy.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.genealogy.auth.infrastructure.persistence.entity.RoleEntity;
import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import com.genealogy.auth.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import com.genealogy.auth.infrastructure.persistence.repository.JpaRoleRepository;
import com.genealogy.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.genealogy.common.exception.BusinessException;
import com.genealogy.security.JwtService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JpaUserRepository userRepository;
  private final JpaRoleRepository roleRepository;
  private final JpaRefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new BusinessException("Username already exists");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new BusinessException("Email already exists");
    }

    RoleEntity defaultRole =
        roleRepository
            .findByCode("ROLE_MEMBER")
            .orElseThrow(() -> new BusinessException("Default role is not configured"));

    UserEntity user =
        UserEntity.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .enabled(true)
            .build();
    user.getRoles().add(defaultRole);

    UserEntity savedUser = userRepository.save(user);
    return issueTokens(savedUser);
  }

  @Transactional
  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    UserEntity user =
        userRepository
            .findByUsername(request.username())
            .orElseThrow(() -> new BusinessException("Invalid username or password"));

    return issueTokens(user);
  }

  @Transactional
  public void logout(LogoutRequest request) {
    refreshTokenRepository
        .findByToken(request.refreshToken())
        .ifPresent(
            refreshToken -> {
              refreshToken.setRevoked(true);
              refreshTokenRepository.save(refreshToken);
            });
  }

  @Transactional
  public AuthResponse refresh(RefreshTokenRequest request) {
    RefreshTokenEntity tokenEntity =
        refreshTokenRepository
            .findByToken(request.refreshToken())
            .orElseThrow(() -> new BusinessException("Invalid refresh token"));
    if (tokenEntity.isRevoked() || tokenEntity.getExpiryAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException("Refresh token is expired or revoked");
    }

    tokenEntity.setRevoked(true);
    refreshTokenRepository.save(tokenEntity);
    return issueTokens(tokenEntity.getUser());
  }

  private AuthResponse issueTokens(UserEntity user) {
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    RefreshTokenEntity refreshTokenEntity =
        RefreshTokenEntity.builder()
            .user(user)
            .token(refreshToken)
            .expiryAt(LocalDateTime.now().plus(Duration.ofMillis(jwtService.getRefreshExpirationMs())))
            .revoked(false)
            .build();
    refreshTokenRepository.save(refreshTokenEntity);

    Set<String> roles =
        user.getRoles().stream().map(RoleEntity::getCode).collect(Collectors.toCollection(java.util.LinkedHashSet::new));

    return new AuthResponse(
        accessToken, refreshToken, "Bearer", jwtService.getAccessExpirationMs(), roles);
  }
}
