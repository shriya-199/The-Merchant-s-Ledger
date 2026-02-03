package com.merchantsledger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleTokenInfo {
  private String aud;
  private String email;
  private String name;
  private String sub;
  private String picture;
  @JsonProperty("email_verified")
  private String emailVerified;

  public String getAud() {
    return aud;
  }

  public void setAud(String aud) {
    this.aud = aud;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(String emailVerified) {
    this.emailVerified = emailVerified;
  }
}
