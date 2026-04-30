package com.genealogy.security;

import com.genealogy.auth.infrastructure.persistence.entity.PermissionEntity;
import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import com.genealogy.auth.infrastructure.persistence.repository.JpaUserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final JpaUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Set<GrantedAuthority> authorities =
        user.getRoles().stream()
            .flatMap(
                role ->
                    java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(new SimpleGrantedAuthority(role.getCode())),
                        role.getPermissions().stream()
                            .map(PermissionEntity::getCode)
                            .map(SimpleGrantedAuthority::new)))
            .collect(Collectors.toSet());

    return User.builder()
        .username(user.getUsername())
        .password(user.getPasswordHash())
        .disabled(!user.isEnabled())
        .authorities(authorities)
        .build();
  }
}
