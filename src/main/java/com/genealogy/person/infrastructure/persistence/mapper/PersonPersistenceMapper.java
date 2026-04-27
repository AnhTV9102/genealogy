package com.genealogy.person.infrastructure.persistence.mapper;

import com.genealogy.person.domain.model.Gender;
import com.genealogy.person.domain.model.Person;
import com.genealogy.person.infrastructure.persistence.entity.PersonEntity;

public class PersonPersistenceMapper {

    public static PersonEntity toEntity(Person person) {
        return PersonEntity.builder()
                .id(person.getId())
                .name(person.getFullName())
                .gender(person.getGender().name())
                .birthDate(person.getDateOfBirth())
                .deathDate(person.getDateOfDeath())
                .build();
    }

    public static Person toDomain(PersonEntity entity) {

        if (entity == null) return null;

        return Person.restore(
                entity.getId(),
                entity.getName(),
                Gender.valueOf(entity.getGender().toUpperCase()),
                entity.getBirthDate(),
                entity.getDeathDate()
        );
    }
}
