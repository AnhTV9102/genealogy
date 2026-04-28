package com.genealogy.person.domain.repository;

import com.genealogy.person.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonRepository {
    Person save(Person person);
    Optional<Person> findById(Long id);
    void deleteById(Long id);
    Page<Person> findAll(Pageable pageable);
}
