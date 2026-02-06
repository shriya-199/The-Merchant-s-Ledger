package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.AuthRequest;
import com.merchantsledger.dto.AuthResponse;
import com.merchantsledger.dto.ForgotPasswordResetRequest;
import com.merchantsledger.dto.ForgotPasswordSendRequest;
import com.merchantsledger.dto.GoogleAuthRequest;
import com.merchantsledger.dto.OtpChallengeResponse;
import com.merchantsledger.dto.OtpSendRequest;
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

  @PostMapping("/otp/send")
  public OtpChallengeResponse sendOtp(@Valid @RequestBody OtpSendRequest request) {
    return authService.sendOtp(request.getEmail(), request.getPhone(), request.getPurpose());
  }

  @PostMapping("/login/send-otp")
  public OtpChallengeResponse sendLoginOtp(@Valid @RequestBody AuthRequest request) {
    return authService.sendLoginOtp(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @PostMapping("/google")
  public AuthResponse google(@Valid @RequestBody GoogleAuthRequest request) {
    return authService.googleAuth(request.getCredential());
  }

  @PostMapping("/forgot-password/send-otp")
  public OtpChallengeResponse sendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordSendRequest request) {
    return authService.sendForgotPasswordOtp(request);
  }

  @PostMapping("/forgot-password/reset")
  public void resetForgotPassword(@Valid @RequestBody ForgotPasswordResetRequest request) {
    authService.resetForgottenPassword(request);
  }

  @GetMapping("/me")
  public UserResponse me(@AuthenticationPrincipal User user) {
    return userService.toResponse(user);
  }
}
