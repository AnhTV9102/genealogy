package com.genealogy.relationship.api;

import com.genealogy.person.application.dto.PersonResponse;
import com.genealogy.relationship.application.dto.CreateRelationshipRequest;
import com.genealogy.relationship.application.dto.RelationshipResponse;
import com.genealogy.relationship.application.service.RelationshipService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RelationshipController {

  private final RelationshipService relationshipService;

  @PostMapping("/relationships")
  @PreAuthorize("hasAuthority('RELATIONSHIP_WRITE')")
  public RelationshipResponse create(@RequestBody @Valid CreateRelationshipRequest request) {
    return relationshipService.create(request);
  }

  @GetMapping("/relationships/{id}")
  @PreAuthorize("hasAuthority('RELATIONSHIP_READ')")
  public RelationshipResponse getById(@PathVariable Long id) {
    return relationshipService.getById(id);
  }

  @DeleteMapping("/relationships/{id}")
  @PreAuthorize("hasAuthority('RELATIONSHIP_WRITE')")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    relationshipService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/persons/{personId}/relationships")
  @PreAuthorize("hasAuthority('RELATIONSHIP_READ')")
  public List<RelationshipResponse> getRelationshipsByPersonId(@PathVariable Long personId) {
    return relationshipService.getRelationshipsByPersonId(personId);
  }

  @GetMapping("/persons/{personId}/parents")
  @PreAuthorize("hasAuthority('RELATIONSHIP_READ')")
  public List<PersonResponse> getParents(@PathVariable Long personId) {
    return relationshipService.getParents(personId);
  }
}
