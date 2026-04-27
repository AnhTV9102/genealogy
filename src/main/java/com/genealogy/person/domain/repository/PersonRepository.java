package com.genealogy.person.domain.repository;

import com.genealogy.person.domain.model.Person;

public interface PersonRepository {
    Person save(Person person);
}
