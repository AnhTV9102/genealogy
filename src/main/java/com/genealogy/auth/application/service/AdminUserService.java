package com.genealogy.auth.application.service;

import com.genealogy.auth.application.dto.AdminCreateUserRequest;
import com.genealogy.auth.infrastructure.persistence.entity.RoleEntity;
import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import com.genealogy.auth.infrastructure.persistence.repository.JpaRoleRepository;
import com.genealogy.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.genealogy.common.exception.BusinessException;
import com.genealogy.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

  private final JpaUserRepository userRepository;
  private final JpaRoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void createUser(AdminCreateUserRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new BusinessException("Username already exists");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new BusinessException("Email already exists");
    }

    RoleEntity memberRole =
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
    user.getRoles().add(memberRole);
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(Long userId) {
    if (userRepository.findById(userId).isEmpty()) {
      throw new ResourceNotFoundException("User", userId);
    }
    userRepository.deleteById(userId);
  }
}
