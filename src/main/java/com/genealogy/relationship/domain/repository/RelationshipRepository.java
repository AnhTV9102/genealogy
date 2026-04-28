package com.genealogy.relationship.domain.repository;

import com.genealogy.relationship.domain.model.Relationship;
import java.util.List;
import java.util.Optional;

public interface RelationshipRepository {
    Relationship save(Relationship relationship);
    Optional<Relationship> findById(Long id);
    void deleteById(Long id);
    boolean existsByFromPersonIdAndToPersonIdAndType(Long fromPersonId, Long toPersonId, String type);
    List<Relationship> findByPersonId(Long personId);
    List<Relationship> findParentsByPersonId(Long personId);
    List<Relationship> findChildrenByPersonId(Long personId);
    List<Relationship> findSpouseByPersonId(Long personId);
}