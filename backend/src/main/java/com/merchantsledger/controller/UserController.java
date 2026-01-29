package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.dto.UserUpdateRequest;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping("/me")
  public UserResponse updateMe(@AuthenticationPrincipal User user,
                               @RequestBody UserUpdateRequest request) {
    return userService.updateProfile(user, request);
  }
}
