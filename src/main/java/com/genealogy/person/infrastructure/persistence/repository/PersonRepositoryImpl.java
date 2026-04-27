package com.genealogy.person.infrastructure.persistence.repository;

import com.genealogy.person.domain.model.Person;
import com.genealogy.person.domain.repository.PersonRepository;
import com.genealogy.person.infrastructure.persistence.mapper.PersonPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final JpaPersonRepository jpaRepository;

    @Override
    public Person save(Person person) {
        var entity = PersonPersistenceMapper.toEntity(person);
        var saved = jpaRepository.save(entity);
        return PersonPersistenceMapper.toDomain(saved);
    }
}