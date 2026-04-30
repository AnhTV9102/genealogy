package com.genealogy.security;

import com.genealogy.auth.infrastructure.persistence.entity.PermissionEntity;
import com.genealogy.auth.infrastructure.persistence.entity.RoleEntity;
import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration}")
  private long accessExpirationMs;

  @Value("${app.jwt.refresh-expiration}")
  private long refreshExpirationMs;

  public String generateAccessToken(UserEntity user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(
        "roles",
        user.getRoles().stream().map(RoleEntity::getCode).collect(Collectors.toCollection(LinkedHashSet::new)));
    claims.put(
        "permissions",
        user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(PermissionEntity::getCode)
            .collect(Collectors.toCollection(LinkedHashSet::new)));
    return buildToken(claims, user.getUsername(), accessExpirationMs);
  }

  public String generateRefreshToken(UserEntity user) {
    return buildToken(Collections.emptyMap(), user.getUsername(), refreshExpirationMs);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails user) {
    String username = extractUsername(token);
    return username.equals(user.getUsername()) && !isTokenExpired(token);
  }

  public long getAccessExpirationMs() {
    return accessExpirationMs;
  }

  public long getRefreshExpirationMs() {
    return refreshExpirationMs;
  }

  private String buildToken(Map<String, Object> claims, String subject, long expiration) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expiration);
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getSigningKey())
        .compact();
  }

  private boolean isTokenExpired(String token) {
    Date expiration = extractClaim(token, Claims::getExpiration);
    return expiration.before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith((javax.crypto.SecretKey) getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
