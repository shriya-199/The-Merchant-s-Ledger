package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.dto.UserUpdateRequest;
import com.merchantsledger.dto.CompleteProfileRequest;
import com.merchantsledger.dto.DeleteAccountRequest;
import com.merchantsledger.dto.OtpChallengeResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.UserService;
import jakarta.validation.Valid;

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

  @PutMapping("/me/complete-profile")
  public UserResponse completeProfile(@AuthenticationPrincipal User user,
                                      @Valid @RequestBody CompleteProfileRequest request) {
    return userService.completeProfile(user, request);
  }

  @PostMapping("/me/delete/send-otp")
  public OtpChallengeResponse sendDeleteOtp(@AuthenticationPrincipal User user) {
    return userService.sendDeleteAccountOtp(user);
  }

  @PostMapping("/me/delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(@AuthenticationPrincipal User user,
                            @Valid @RequestBody DeleteAccountRequest request) {
    userService.deleteAccount(user, request);
  }
}
