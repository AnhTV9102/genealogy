package com.genealogy.relationship.infrastructure.persistence.repository;

import com.genealogy.relationship.infrastructure.persistence.entity.RelationshipEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaRelationshipRepository extends JpaRepository<RelationshipEntity, Long> {

  @Query(
      "SELECT COUNT(r) > 0 FROM RelationshipEntity r WHERE r.fromPersonId = :fromPersonId AND r.toPersonId = :toPersonId AND r.type = :type")
  boolean existsByFromPersonIdAndToPersonIdAndType(
      @Param("fromPersonId") Long fromPersonId,
      @Param("toPersonId") Long toPersonId,
      @Param("type") String type);

  @Query(
      "SELECT r FROM RelationshipEntity r WHERE r.fromPersonId = :personId OR r.toPersonId = :personId")
  List<RelationshipEntity> findByPersonId(@Param("personId") Long personId);

  @Query(
      "SELECT r FROM RelationshipEntity r WHERE r.toPersonId = :personId AND r.type = 'PARENT_OF'")
  List<RelationshipEntity> findParentsByPersonId(@Param("personId") Long personId);

  @Query(
      "SELECT r FROM RelationshipEntity r WHERE r.fromPersonId = :personId AND r.type = 'PARENT_OF'")
  List<RelationshipEntity> findChildrenByPersonId(@Param("personId") Long personId);

  @Query(
      "SELECT r FROM RelationshipEntity r WHERE (r.fromPersonId = :personId OR r.toPersonId = :personId) AND r.type = 'SPOUSE_OF'")
  List<RelationshipEntity> findSpouseByPersonId(@Param("personId") Long personId);
}
