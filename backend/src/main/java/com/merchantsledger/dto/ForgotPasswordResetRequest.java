package com.merchantsledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordResetRequest {
  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String emailOtpChallengeId;

  @NotBlank
  private String emailOtpCode;

  @NotBlank
  private String newPassword;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
