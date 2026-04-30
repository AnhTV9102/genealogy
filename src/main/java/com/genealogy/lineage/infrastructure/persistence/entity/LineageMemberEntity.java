package com.genealogy.lineage.infrastructure.persistence.entity;

import com.genealogy.auth.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lineage_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineageMemberEntity {

  @EmbeddedId private LineageMemberId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("lineageId")
  @JoinColumn(name = "lineage_id")
  private LineageEntity lineage;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "role_code", nullable = false)
  private String roleCode;
}
