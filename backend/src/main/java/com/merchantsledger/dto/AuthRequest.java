package com.merchantsledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;
  private String emailOtpChallengeId;
  private String emailOtpCode;
  private String phoneOtpChallengeId;
  private String phoneOtpCode;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmailOtpChallengeId() {
    return emailOtpChallengeId;
  }

  public void setEmailOtpChallengeId(String emailOtpChallengeId) {
    this.emailOtpChallengeId = emailOtpChallengeId;
  }

  public String getEmailOtpCode() {
    return emailOtpCode;
  }

  public void setEmailOtpCode(String emailOtpCode) {
    this.emailOtpCode = emailOtpCode;
  }

  public String getPhoneOtpChallengeId() {
    return phoneOtpChallengeId;
  }

  public void setPhoneOtpChallengeId(String phoneOtpChallengeId) {
    this.phoneOtpChallengeId = phoneOtpChallengeId;
  }

  public String getPhoneOtpCode() {
    return phoneOtpCode;
  }

  public void setPhoneOtpCode(String phoneOtpCode) {
    this.phoneOtpCode = phoneOtpCode;
  }
}
