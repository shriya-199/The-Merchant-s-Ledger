package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.AdminUserUpdateRequest;
import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/users")
  public List<UserResponse> listUsers(@AuthenticationPrincipal User actor) {
    return adminService.listUsers(actor);
  }

  @PutMapping("/users/{id}")
  public UserResponse updateUser(@AuthenticationPrincipal User actor, @PathVariable Long id, @RequestBody AdminUserUpdateRequest request) {
    return adminService.updateUser(actor, id, request);
  }
}
