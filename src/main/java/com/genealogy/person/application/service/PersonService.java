package com.genealogy.person.application.service;

import com.genealogy.person.application.dto.CreatePersonRequest;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.dto.UpdatePersonRequest;
import com.genealogy.person.application.mapper.PersonMapper;
import com.genealogy.person.domain.model.Gender;
import com.genealogy.person.domain.model.Person;
import com.genealogy.person.domain.repository.PersonRepository;
import com.genealogy.person.infrastructure.persistence.repository.PersonRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonRepositoryImpl personRepositoryImpl;

    public PersonResponse create(CreatePersonRequest request) {

        Person person = Person.create(
                request.fullName(),
                Gender.valueOf(request.gender().toUpperCase()),
                request.dateOfBirth()
        );

        Person saved = personRepository.save(person);

        return PersonMapper.toResponse(saved);
    }

    public PersonResponse getById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        return PersonMapper.toResponse(person);
    }

    public PersonResponse update(Long id, UpdatePersonRequest request) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            person.changeName(request.fullName());
        }

        if (request.dateOfDeath() != null) {
            person.markAsDeceased(request.dateOfDeath());
        }

        Person updated = personRepository.save(person);
        return PersonMapper.toResponse(updated);
    }

    public void deleteById(Long id) {
        if (personRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    public Page<PersonResponse> listAll(Pageable pageable) {
        return personRepositoryImpl.findAllPaginated(pageable)
                .map(PersonMapper::toResponse);
    }
}