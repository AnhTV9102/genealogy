package com.genealogy.person.api;

import com.genealogy.person.application.dto.CreatePersonRequest;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.service.PersonService;
import lombok.RequiredArgsConstructor;
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
}
