package com.genealogy.auth.api;

import com.genealogy.auth.application.dto.AdminCreateUserRequest;
import com.genealogy.auth.application.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminUserService adminUserService;

  @PostMapping("/users")
  public ResponseEntity<Void> createUser(@RequestBody @Valid AdminCreateUserRequest request) {
    adminUserService.createUser(request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    adminUserService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
