package com.genealogy.lineage.api;

import com.genealogy.lineage.application.dto.*;
import com.genealogy.lineage.application.service.LineageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lineages")
@RequiredArgsConstructor
public class LineageController {

  private final LineageService lineageService;

  @PostMapping
  public LineageResponse createLineage(
      @RequestBody @Valid CreateLineageRequest request, Authentication authentication) {
    return lineageService.createLineage(request.name(), authentication.getName());
  }

  @PostMapping("/{lineageId}/members")
  @PreAuthorize("@lineageAccessService.canManageLineage(#lineageId, authentication.name)")
  public ResponseEntity<Void> addMember(
      @PathVariable Long lineageId, @RequestBody @Valid AddLineageMemberRequest request) {
    lineageService.addMember(lineageId, request.userId(), request.roleCode());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{lineageId}/permissions")
  @PreAuthorize("@lineageAccessService.canManageLineage(#lineageId, authentication.name)")
  public ResponseEntity<Void> grantPermission(
      @PathVariable Long lineageId, @RequestBody @Valid GrantLineagePermissionRequest request) {
    lineageService.grantPermission(lineageId, request.userId(), request.permissionCode());
    return ResponseEntity.noContent().build();
  }
}
