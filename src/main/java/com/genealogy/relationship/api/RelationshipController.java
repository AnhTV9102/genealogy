package com.genealogy.relationship.api;

import com.genealogy.relationship.application.dto.CreateRelationshipRequest;
import com.genealogy.relationship.application.dto.RelationshipResponse;
import com.genealogy.relationship.application.service.RelationshipService;
import com.genealogy.person.application.dto.PersonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping("/relationships")
    public RelationshipResponse create(@RequestBody @Valid CreateRelationshipRequest request) {
        return relationshipService.create(request);
    }

    @GetMapping("/relationships/{id}")
    public RelationshipResponse getById(@PathVariable Long id) {
        return relationshipService.getById(id);
    }

    @DeleteMapping("/relationships/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        relationshipService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/persons/{personId}/relationships")
    public List<RelationshipResponse> getRelationshipsByPersonId(@PathVariable Long personId) {
        return relationshipService.getRelationshipsByPersonId(personId);
    }

    @GetMapping("/persons/{personId}/parents")
    public List<PersonResponse> getParents(@PathVariable Long personId) {
        return relationshipService.getParents(personId);
    }
}
