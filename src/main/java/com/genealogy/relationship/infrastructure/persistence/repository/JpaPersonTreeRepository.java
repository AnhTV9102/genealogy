package com.genealogy.relationship.infrastructure.persistence.repository;

import com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntity;
import com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** JPA Repository for person_tree table queries (Closure Table pattern). */
public interface JpaPersonTreeRepository
    extends JpaRepository<PersonTreeEntity, PersonTreeEntityId> {

  /** Find all ancestors of a person (excluding self), ordered by depth ascending. */
  @Query(
      "SELECT pt FROM PersonTreeEntity pt "
          + "WHERE pt.descendantId = :personId AND pt.depth > 0 "
          + "ORDER BY pt.depth ASC")
  List<PersonTreeEntity> findAncestors(@Param("personId") Long personId);

  /** Find all descendants of a person (excluding self), ordered by depth ascending. */
  @Query(
      "SELECT pt FROM PersonTreeEntity pt "
          + "WHERE pt.ancestorId = :personId AND pt.depth > 0 "
          + "ORDER BY pt.depth ASC")
  List<PersonTreeEntity> findDescendants(@Param("personId") Long personId);

  /** Find ancestors up to a specific depth. */
  @Query(
      "SELECT pt FROM PersonTreeEntity pt "
          + "WHERE pt.descendantId = :personId AND pt.depth > 0 AND pt.depth <= :maxDepth "
          + "ORDER BY pt.depth ASC")
  List<PersonTreeEntity> findAncestorsMaxDepth(
      @Param("personId") Long personId, @Param("maxDepth") Integer maxDepth);

  /** Find descendants up to a specific depth. */
  @Query(
      "SELECT pt FROM PersonTreeEntity pt "
          + "WHERE pt.ancestorId = :personId AND pt.depth > 0 AND pt.depth <= :maxDepth "
          + "ORDER BY pt.depth ASC")
  List<PersonTreeEntity> findDescendantsMaxDepth(
      @Param("personId") Long personId, @Param("maxDepth") Integer maxDepth);
}
