package com.merchantsledger.dto;

public class UserUpdateRequest {
  private String fullName;
  private String phone;
  private String companyName;
  private String address;
  private String roleTitle;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
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
}
