package com.genealogy.lineage.infrastructure.persistence.repository;

import com.genealogy.lineage.infrastructure.persistence.entity.LineageMemberEntity;
import com.genealogy.lineage.infrastructure.persistence.entity.LineageMemberId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineageMemberRepository
    extends JpaRepository<LineageMemberEntity, LineageMemberId> {
  Optional<LineageMemberEntity> findByIdLineageIdAndIdUserId(Long lineageId, Long userId);
}
