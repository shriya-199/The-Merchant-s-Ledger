package com.merchantsledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
  @NotBlank
  private String fullName;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;

  private String phone;
  private String companyName;
  private String address;
  private String roleTitle;
  private String roleName;
  @NotBlank
  private String emailOtpChallengeId;
  @NotBlank
  private String emailOtpCode;
  @NotBlank
  private String phoneOtpChallengeId;
  @NotBlank
  private String phoneOtpCode;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRoleTitle() {
    return roleTitle;
  }

  public void setRoleTitle(String roleTitle) {
    this.roleTitle = roleTitle;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
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
