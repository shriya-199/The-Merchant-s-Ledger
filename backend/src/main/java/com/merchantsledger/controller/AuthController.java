package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.AuthRequest;
import com.merchantsledger.dto.AuthResponse;
import com.merchantsledger.dto.RegisterRequest;
import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.AuthService;
import com.merchantsledger.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @GetMapping("/me")
  public UserResponse me(@AuthenticationPrincipal User user) {
    return userService.toResponse(user);
  }
}
