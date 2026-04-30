package com.genealogy.lineage.application.service;

import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import com.genealogy.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.genealogy.common.exception.BusinessException;
import com.genealogy.common.exception.ResourceNotFoundException;
import com.genealogy.lineage.application.dto.LineageResponse;
import com.genealogy.lineage.infrastructure.persistence.entity.*;
import com.genealogy.lineage.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LineageService {

  private final JpaUserRepository userRepository;
  private final JpaLineageRepository lineageRepository;
  private final JpaLineageMemberRepository lineageMemberRepository;
  private final JpaLineageMemberPermissionRepository lineageMemberPermissionRepository;

  @Transactional
  public LineageResponse createLineage(String name, String creatorUsername) {
    UserEntity creator =
        userRepository
            .findByUsername(creatorUsername)
            .orElseThrow(() -> new BusinessException("Creator does not exist"));
    LineageEntity lineage = LineageEntity.builder().name(name).createdBy(creator).build();
    LineageEntity saved = lineageRepository.save(lineage);

    LineageMemberEntity ownerMember =
        LineageMemberEntity.builder()
            .id(new LineageMemberId(saved.getId(), creator.getId()))
            .lineage(saved)
            .user(creator)
            .roleCode("OWNER")
            .build();
    lineageMemberRepository.save(ownerMember);

    return new LineageResponse(saved.getId(), saved.getName(), creator.getUsername());
  }

  @Transactional
  public void addMember(Long lineageId, Long userId, String roleCode) {
    LineageEntity lineage =
        lineageRepository.findById(lineageId).orElseThrow(() -> new ResourceNotFoundException("Lineage", lineageId));
    UserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    LineageMemberEntity member =
        LineageMemberEntity.builder()
            .id(new LineageMemberId(lineageId, userId))
            .lineage(lineage)
            .user(user)
            .roleCode(roleCode.toUpperCase())
            .build();
    lineageMemberRepository.save(member);
  }

  @Transactional
  public void grantPermission(Long lineageId, Long userId, String permissionCode) {
    if (lineageMemberRepository.findByIdLineageIdAndIdUserId(lineageId, userId).isEmpty()) {
      throw new BusinessException("User is not a member of this lineage");
    }
    LineageMemberPermissionEntity permission =
        LineageMemberPermissionEntity.builder()
            .id(new LineageMemberPermissionId(lineageId, userId, permissionCode.toUpperCase()))
            .build();
    lineageMemberPermissionRepository.save(permission);
  }
}
