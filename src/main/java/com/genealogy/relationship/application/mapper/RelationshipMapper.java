package com.genealogy.relationship.application.mapper;

import com.genealogy.relationship.application.dto.RelationshipResponse;
import com.genealogy.relationship.domain.model.Relationship;

public class RelationshipMapper {

  public static RelationshipResponse toResponse(Relationship relationship) {
    return new RelationshipResponse(
        relationship.getId(),
        relationship.getFromPersonId(),
        relationship.getToPersonId(),
        relationship.getType().name(),
        relationship.getCreatedAt());
  }
}
