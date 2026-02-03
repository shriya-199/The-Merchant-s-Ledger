package com.merchantsledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OtpSendRequest {
  @Email
  @NotBlank
  private String email;
  @NotBlank
  private String phone;
  @NotNull
  private OtpPurpose purpose;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public OtpPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(OtpPurpose purpose) {
    this.purpose = purpose;
  }
}
