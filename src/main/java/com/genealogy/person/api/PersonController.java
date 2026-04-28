package com.genealogy.person.api;

import com.genealogy.person.application.dto.CreatePersonRequest;
import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.person.application.dto.UpdatePersonRequest;
import com.genealogy.person.application.service.PersonService;
import com.genealogy.relationship.application.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final RelationshipService relationshipService;

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

    @GetMapping("/{personId}/children")
    public List<PersonResponse> getChildren(@PathVariable Long personId) {
        return relationshipService.getChildren(personId);
    }

    @GetMapping("/{personId}/spouse")
    public ResponseEntity<PersonResponse> getSpouse(@PathVariable Long personId) {
        PersonResponse spouse = relationshipService.getSpouse(personId);
        if (spouse == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(spouse);
    }

    @GetMapping("/{personId}/ancestors")
    public List<PersonResponse> getAncestors(
            @PathVariable Long personId,
            @RequestParam(required = false) Integer generations) {
        return relationshipService.getAncestors(personId, generations);
    }

    @GetMapping("/{personId}/descendants")
    public List<PersonResponse> getDescendants(
            @PathVariable Long personId,
            @RequestParam(required = false) Integer generations) {
        return relationshipService.getDescendants(personId, generations);
    }
}
