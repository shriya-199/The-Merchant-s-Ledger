package com.merchantsledger.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteAccountRequest {
  @NotBlank
  private String phoneOtpChallengeId;
  @NotBlank
  private String phoneOtpCode;

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
