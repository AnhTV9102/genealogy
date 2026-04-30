package com.genealogy.lineage.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lineage_member_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineageMemberPermissionEntity {

  @EmbeddedId private LineageMemberPermissionId id;
}
