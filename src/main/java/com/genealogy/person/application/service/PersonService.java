package com.genealogy.person.application.service;

import com.genealogy.person.application.dto.CreatePersonRequest;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.mapper.PersonMapper;
import com.genealogy.person.domain.model.Gender;
import com.genealogy.person.domain.model.Person;
import com.genealogy.person.domain.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public PersonResponse create(CreatePersonRequest request) {

        Person person = Person.create(
                request.fullName(),
                Gender.valueOf(request.gender().toUpperCase()),
                request.dateOfBirth()
        );

        Person saved = personRepository.save(person);

        return PersonMapper.toResponse(saved);
    }
}