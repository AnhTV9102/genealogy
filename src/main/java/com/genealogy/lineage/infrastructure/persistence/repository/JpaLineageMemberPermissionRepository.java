package com.genealogy.lineage.infrastructure.persistence.repository;

import com.genealogy.lineage.infrastructure.persistence.entity.LineageMemberPermissionEntity;
import com.genealogy.lineage.infrastructure.persistence.entity.LineageMemberPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineageMemberPermissionRepository
    extends JpaRepository<LineageMemberPermissionEntity, LineageMemberPermissionId> {
  boolean existsByIdLineageIdAndIdUserIdAndIdPermissionCode(
      Long lineageId, Long userId, String permissionCode);
}
