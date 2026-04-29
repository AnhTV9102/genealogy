package com.genealogy.relationship.infrastructure.persistence.mapper;

import com.genealogy.relationship.domain.model.PersonTree;
import com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntity;

/** Mapper for converting between PersonTree domain model and PersonTreeEntity. */
public class PersonTreePersistenceMapper {

  private PersonTreePersistenceMapper() {
    // Utility class
  }

  /** Convert PersonTreeEntity to PersonTree domain model. */
  public static PersonTree toDomain(PersonTreeEntity entity) {
    if (entity == null) {
      return null;
    }
    return new PersonTree(entity.getAncestorId(), entity.getDescendantId(), entity.getDepth());
  }

  /** Convert PersonTree domain model to PersonTreeEntity. */
  public static PersonTreeEntity toEntity(PersonTree domain) {
    if (domain == null) {
      return null;
    }
    return new PersonTreeEntity(
        domain.getAncestorId(), domain.getDescendantId(), domain.getDepth());
  }
}
