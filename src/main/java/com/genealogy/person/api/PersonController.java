package com.genealogy.person.api;

import com.genealogy.person.application.dto.CreatePersonRequest;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.dto.UpdatePersonRequest;
import com.genealogy.person.application.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public PersonResponse create(@RequestBody CreatePersonRequest request) {
        return personService.create(request);
    }

    @GetMapping
    public Page<PersonResponse> listAll(Pageable pageable) {
        return personService.listAll(pageable);
    }

    @GetMapping("/{id}")
    public PersonResponse getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable Long id, @RequestBody UpdatePersonRequest request) {
        return personService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
