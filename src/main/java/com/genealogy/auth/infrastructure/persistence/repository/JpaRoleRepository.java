package com.genealogy.auth.infrastructure.persistence.repository;

import com.genealogy.auth.infrastructure.persistence.entity.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findByCode(String code);
}
