package com.genealogy.person.infrastructure.persistence.repository;

import com.genealogy.person.domain.model.Person;
import com.genealogy.person.domain.repository.PersonRepository;
import com.genealogy.person.infrastructure.persistence.mapper.PersonPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Override
    public Optional<Person> findById(Long id) {
        return jpaRepository.findById(id)
                .map(PersonPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Person> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(PersonPersistenceMapper::toDomain);
    }

    public Page<Person> findAllPaginated(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(PersonPersistenceMapper::toDomain);
    }
}