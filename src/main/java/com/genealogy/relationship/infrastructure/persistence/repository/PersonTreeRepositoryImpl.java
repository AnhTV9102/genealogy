package com.genealogy.relationship.infrastructure.persistence.repository;

import com.genealogy.relationship.domain.model.PersonTree;
import com.genealogy.relationship.domain.repository.PersonTreeRepository;
import com.genealogy.relationship.infrastructure.persistence.mapper.PersonTreePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure implementation of PersonTreeRepository using JPA.
 */
@Repository
@RequiredArgsConstructor
public class PersonTreeRepositoryImpl implements PersonTreeRepository {

    private final JpaPersonTreeRepository jpaRepository;

    @Override
    public PersonTree save(PersonTree personTree) {
        var entity = PersonTreePersistenceMapper.toEntity(personTree);
        var saved = jpaRepository.save(entity);
        return PersonTreePersistenceMapper.toDomain(saved);
    }

    @Override
    public List<PersonTree> findAncestors(Long personId, Integer maxDepth) {
        List<?> entities;
        if (maxDepth == null) {
            entities = jpaRepository.findAncestors(personId);
        } else {
            entities = jpaRepository.findAncestorsMaxDepth(personId, maxDepth);
        }
        return entities.stream()
                .map(e -> PersonTreePersistenceMapper.toDomain((com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntity) e))
                .toList();
    }

    @Override
    public List<PersonTree> findDescendants(Long personId, Integer maxDepth) {
        List<?> entities;
        if (maxDepth == null) {
            entities = jpaRepository.findDescendants(personId);
        } else {
            entities = jpaRepository.findDescendantsMaxDepth(personId, maxDepth);
        }
        return entities.stream()
                .map(e -> PersonTreePersistenceMapper.toDomain((com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntity) e))
                .toList();
    }

    @Override
    public List<PersonTree> findAncestorsGenerations(Long personId, int generations) {
        return findAncestors(personId, generations);
    }

    @Override
    public List<PersonTree> findDescendantsGenerations(Long personId, int generations) {
        return findDescendants(personId, generations);
    }

    @Override
    public Optional<PersonTree> findByAncestorAndDescendant(Long ancestorId, Long descendantId) {
        return jpaRepository.findById(new com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntityId(ancestorId, descendantId))
                .map(PersonTreePersistenceMapper::toDomain);
    }

    @Override
    public void delete(Long ancestorId, Long descendantId) {
        jpaRepository.deleteById(new com.genealogy.relationship.infrastructure.persistence.entity.PersonTreeEntityId(ancestorId, descendantId));
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}

