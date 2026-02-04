package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.merchantsledger.exception.ForbiddenException;
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
                               Authentication authentication,
                               @RequestBody UserUpdateRequest request) {
    return userService.updateProfile(resolveUser(user, authentication), request);
  }

  @PutMapping("/me/complete-profile")
  public UserResponse completeProfile(@AuthenticationPrincipal User user,
                                      Authentication authentication,
                                      @Valid @RequestBody CompleteProfileRequest request) {
    return userService.completeProfile(resolveUser(user, authentication), request);
  }

  @PostMapping("/me/delete/send-otp")
  public OtpChallengeResponse sendDeleteOtp(@AuthenticationPrincipal User user,
                                            Authentication authentication) {
    return userService.sendDeleteAccountOtp(resolveUser(user, authentication));
  }

  @PostMapping("/me/delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(@AuthenticationPrincipal User user,
                            Authentication authentication,
                            @Valid @RequestBody DeleteAccountRequest request) {
    userService.deleteAccount(resolveUser(user, authentication), request);
  }

  private User resolveUser(User user, Authentication authentication) {
    if (user != null) {
      return user;
    }
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ForbiddenException("Authentication required");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof User principalUser) {
      return principalUser;
    }
    if (principal instanceof UserDetails userDetails) {
      return (User) userService.loadUserByUsername(userDetails.getUsername());
    }
    if (principal instanceof String username && !"anonymousUser".equalsIgnoreCase(username)) {
      return (User) userService.loadUserByUsername(username);
    }
    throw new ForbiddenException("Authentication required");
  }
}
