package com.genealogy.relationship.infrastructure.persistence.mapper;

import com.genealogy.relationship.domain.model.Relationship;
import com.genealogy.relationship.domain.model.RelationshipType;
import com.genealogy.relationship.infrastructure.persistence.entity.RelationshipEntity;

public class RelationshipPersistenceMapper {

  public static RelationshipEntity toEntity(Relationship relationship) {
    return RelationshipEntity.builder()
        .id(relationship.getId())
        .fromPersonId(relationship.getFromPersonId())
        .toPersonId(relationship.getToPersonId())
        .type(relationship.getType().name())
        .createdAt(relationship.getCreatedAt())
        .build();
  }

  public static Relationship toDomain(RelationshipEntity entity) {

    if (entity == null) return null;

    return Relationship.restore(
        entity.getId(),
        entity.getFromPersonId(),
        entity.getToPersonId(),
        RelationshipType.valueOf(entity.getType().toUpperCase()),
        entity.getCreatedAt());
  }
}
