package com.genealogy.lineage.application.service;

import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import com.genealogy.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.genealogy.lineage.infrastructure.persistence.entity.LineageMemberEntity;
import com.genealogy.lineage.infrastructure.persistence.repository.JpaLineageMemberPermissionRepository;
import com.genealogy.lineage.infrastructure.persistence.repository.JpaLineageMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("lineageAccessService")
@RequiredArgsConstructor
public class LineageAccessService {

  private final JpaUserRepository userRepository;
  private final JpaLineageMemberRepository lineageMemberRepository;
  private final JpaLineageMemberPermissionRepository lineageMemberPermissionRepository;

  public boolean canManageLineage(Long lineageId, String username) {
    UserEntity user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
      return false;
    }
    LineageMemberEntity member =
        lineageMemberRepository.findByIdLineageIdAndIdUserId(lineageId, user.getId()).orElse(null);
    if (member == null) {
      return false;
    }
    if ("OWNER".equalsIgnoreCase(member.getRoleCode()) || "MANAGER".equalsIgnoreCase(member.getRoleCode())) {
      return true;
    }
    return lineageMemberPermissionRepository.existsByIdLineageIdAndIdUserIdAndIdPermissionCode(
        lineageId, user.getId(), "LINEAGE_MANAGE");
  }
}
