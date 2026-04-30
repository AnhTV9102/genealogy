package com.genealogy.auth.application.dto;

import java.util.Set;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInMs,
    Set<String> roles) {}
