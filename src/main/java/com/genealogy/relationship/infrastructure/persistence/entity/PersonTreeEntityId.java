package com.genealogy.relationship.infrastructure.persistence.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Composite primary key for PersonTreeEntity. */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PersonTreeEntityId implements Serializable {
  private Long ancestorId;
  private Long descendantId;
}
