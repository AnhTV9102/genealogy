package com.genealogy.relationship.application.service;

import com.genealogy.common.exception.BusinessException;
import com.genealogy.common.exception.ResourceNotFoundException;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.mapper.PersonMapper;
import com.genealogy.person.domain.repository.PersonRepository;
import com.genealogy.relationship.application.dto.CreateRelationshipRequest;
import com.genealogy.relationship.application.dto.RelationshipResponse;
import com.genealogy.relationship.application.mapper.RelationshipMapper;
import com.genealogy.relationship.domain.model.Relationship;
import com.genealogy.relationship.domain.model.RelationshipType;
import com.genealogy.relationship.domain.repository.PersonTreeRepository;
import com.genealogy.relationship.domain.repository.RelationshipRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationshipService {

  private final RelationshipRepository relationshipRepository;
  private final PersonRepository personRepository;
  private final PersonTreeRepository personTreeRepository;

  public RelationshipResponse create(CreateRelationshipRequest request) {
    // Validate that both persons exist
    validatePersonExists(request.fromPersonId());
    validatePersonExists(request.toPersonId());

    // Validate relationship type
    RelationshipType type = RelationshipType.valueOf(request.type().toUpperCase());

    // Check for duplicate relationships
    if (relationshipRepository.existsByFromPersonIdAndToPersonIdAndType(
        request.fromPersonId(), request.toPersonId(), type.name())) {
      throw new BusinessException("Relationship already exists between these persons");
    }

    Relationship relationship =
        Relationship.create(request.fromPersonId(), request.toPersonId(), type);

    Relationship saved = relationshipRepository.save(relationship);

    return RelationshipMapper.toResponse(saved);
  }

  public RelationshipResponse getById(Long id) {
    Relationship relationship =
        relationshipRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Relationship", id));

    return RelationshipMapper.toResponse(relationship);
  }

  public void deleteById(Long id) {
    if (relationshipRepository.findById(id).isEmpty()) {
      throw new ResourceNotFoundException("Relationship", id);
    }
    relationshipRepository.deleteById(id);
  }

  public List<RelationshipResponse> getRelationshipsByPersonId(Long personId) {
    // Validate that person exists
    validatePersonExists(personId);

    return relationshipRepository.findByPersonId(personId).stream()
        .map(RelationshipMapper::toResponse)
        .toList();
  }

  public List<PersonResponse> getParents(Long personId) {
    // Validate that person exists
    validatePersonExists(personId);

    return relationshipRepository.findParentsByPersonId(personId).stream()
        .map(Relationship::getFromPersonId)
        .flatMap(parentId -> personRepository.findById(parentId).stream())
        .map(PersonMapper::toResponse)
        .toList();
  }

  public List<PersonResponse> getChildren(Long personId) {
    // Validate that person exists
    validatePersonExists(personId);

    return relationshipRepository.findChildrenByPersonId(personId).stream()
        .map(Relationship::getToPersonId)
        .flatMap(childId -> personRepository.findById(childId).stream())
        .map(PersonMapper::toResponse)
        .toList();
  }

  public PersonResponse getSpouse(Long personId) {
    // Validate that person exists
    validatePersonExists(personId);

    List<Relationship> spouseRelationships = relationshipRepository.findSpouseByPersonId(personId);

    if (spouseRelationships.isEmpty()) {
      return null;
    }

    // Get the spouse person ID (the one that's not the current person)
    Relationship spouseRelationship = spouseRelationships.get(0);
    Long spouseId =
        spouseRelationship.getFromPersonId().equals(personId)
            ? spouseRelationship.getToPersonId()
            : spouseRelationship.getFromPersonId();

    return personRepository.findById(spouseId).map(PersonMapper::toResponse).orElse(null);
  }

  /**
   * Get all ancestors of a person, ordered by generation (closest ancestors first).
   *
   * @param personId the person ID
   * @param generations optional maximum generations (null for all)
   * @return list of ancestors ordered by generation (parents, then grandparents, etc.)
   */
  public List<PersonResponse> getAncestors(Long personId, Integer generations) {
    validatePersonExists(personId);

    var ancestorTrees = personTreeRepository.findAncestors(personId, generations);

    return ancestorTrees.stream()
        .sorted(Comparator.comparingInt(tree -> tree.getDepth()))
        .flatMap(tree -> personRepository.findById(tree.getAncestorId()).stream())
        .map(PersonMapper::toResponse)
        .toList();
  }

  /**
   * Get all descendants of a person, ordered by generation (closest descendants first).
   *
   * @param personId the person ID
   * @param generations optional maximum generations (null for all)
   * @return list of descendants ordered by generation (children, then grandchildren, etc.)
   */
  public List<PersonResponse> getDescendants(Long personId, Integer generations) {
    validatePersonExists(personId);

    var descendantTrees = personTreeRepository.findDescendants(personId, generations);

    return descendantTrees.stream()
        .sorted(Comparator.comparingInt(tree -> tree.getDepth()))
        .flatMap(tree -> personRepository.findById(tree.getDescendantId()).stream())
        .map(PersonMapper::toResponse)
        .toList();
  }

  private void validatePersonExists(Long personId) {
    if (personRepository.findById(personId).isEmpty()) {
      throw new ResourceNotFoundException("Person", personId);
    }
  }
}
