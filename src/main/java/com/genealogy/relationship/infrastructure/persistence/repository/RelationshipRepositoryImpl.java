package com.genealogy.relationship.infrastructure.persistence.repository;

import com.genealogy.relationship.domain.model.Relationship;
import com.genealogy.relationship.domain.repository.RelationshipRepository;
import com.genealogy.relationship.infrastructure.persistence.mapper.RelationshipPersistenceMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RelationshipRepositoryImpl implements RelationshipRepository {

  private final JpaRelationshipRepository jpaRepository;

  @Override
  public Relationship save(Relationship relationship) {
    var entity = RelationshipPersistenceMapper.toEntity(relationship);
    var saved = jpaRepository.save(entity);
    return RelationshipPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<Relationship> findById(Long id) {
    return jpaRepository.findById(id).map(RelationshipPersistenceMapper::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByFromPersonIdAndToPersonIdAndType(
      Long fromPersonId, Long toPersonId, String type) {
    return jpaRepository.existsByFromPersonIdAndToPersonIdAndType(fromPersonId, toPersonId, type);
  }

  @Override
  public List<Relationship> findByPersonId(Long personId) {
    return jpaRepository.findByPersonId(personId).stream()
        .map(RelationshipPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Relationship> findParentsByPersonId(Long personId) {
    return jpaRepository.findParentsByPersonId(personId).stream()
        .map(RelationshipPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Relationship> findChildrenByPersonId(Long personId) {
    return jpaRepository.findChildrenByPersonId(personId).stream()
        .map(RelationshipPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Relationship> findSpouseByPersonId(Long personId) {
    return jpaRepository.findSpouseByPersonId(personId).stream()
        .map(RelationshipPersistenceMapper::toDomain)
        .toList();
  }
}
