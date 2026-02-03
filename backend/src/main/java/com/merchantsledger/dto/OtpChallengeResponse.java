package com.merchantsledger.dto;

public class OtpChallengeResponse {
  private String emailChallengeId;
  private String phoneChallengeId;
  private String emailHint;
  private String phoneHint;
  private Long expiresInSeconds;
  private String emailDevCode;
  private String phoneDevCode;

  public OtpChallengeResponse(String emailChallengeId, String phoneChallengeId, String emailHint, String phoneHint,
                              Long expiresInSeconds, String emailDevCode, String phoneDevCode) {
    this.emailChallengeId = emailChallengeId;
    this.phoneChallengeId = phoneChallengeId;
    this.emailHint = emailHint;
    this.phoneHint = phoneHint;
    this.expiresInSeconds = expiresInSeconds;
    this.emailDevCode = emailDevCode;
    this.phoneDevCode = phoneDevCode;
  }

  public String getEmailChallengeId() {
    return emailChallengeId;
  }

  public String getPhoneChallengeId() {
    return phoneChallengeId;
  }

  public String getEmailHint() {
    return emailHint;
  }

  public String getPhoneHint() {
    return phoneHint;
  }

  public Long getExpiresInSeconds() {
    return expiresInSeconds;
  }

  public String getEmailDevCode() {
    return emailDevCode;
  }

  public String getPhoneDevCode() {
    return phoneDevCode;
  }
}
