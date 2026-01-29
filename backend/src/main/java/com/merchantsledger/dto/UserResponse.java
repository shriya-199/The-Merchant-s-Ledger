package com.merchantsledger.dto;

import java.util.Set;

public class UserResponse {
  private Long id;
  private String fullName;
  private String email;
  private Set<String> roles;
  private String companyName;
  private String phone;
  private String address;
  private String roleTitle;
  private boolean enabled;

  public UserResponse(Long id, String fullName, String email, Set<String> roles, String companyName,
                      String phone, String address, String roleTitle, boolean enabled) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.roles = roles;
    this.companyName = companyName;
    this.phone = phone;
    this.address = address;
    this.roleTitle = roleTitle;
    this.enabled = enabled;
  }

  public Long getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return email;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }

  public String getRoleTitle() {
    return roleTitle;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
