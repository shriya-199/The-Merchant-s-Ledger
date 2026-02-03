package com.merchantsledger.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleAuthRequest {
  @NotBlank
  private String credential;

  public String getCredential() {
    return credential;
  }

  public void setCredential(String credential) {
    this.credential = credential;
  }
}
