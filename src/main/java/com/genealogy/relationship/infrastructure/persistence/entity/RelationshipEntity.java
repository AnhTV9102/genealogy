package com.genealogy.relationship.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationshipEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "from_person_id", nullable = false)
  private Long fromPersonId;

  @Column(name = "to_person_id", nullable = false)
  private Long toPersonId;

  @Column(nullable = false)
  private String type;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
