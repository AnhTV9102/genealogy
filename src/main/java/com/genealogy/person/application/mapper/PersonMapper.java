package com.genealogy.person.application.mapper;

import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.domain.model.Person;

public class PersonMapper {

  public static PersonResponse toResponse(Person person) {
    return new PersonResponse(
        person.getId(), person.getFullName(), person.getGender().name(), person.getDateOfBirth());
  }
}
