package com.genealogy.lineage.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineageMemberPermissionId implements Serializable {
  @Column(name = "lineage_id")
  private Long lineageId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "permission_code")
  private String permissionCode;
}
