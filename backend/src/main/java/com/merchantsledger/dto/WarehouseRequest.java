package com.merchantsledger.dto;

import jakarta.validation.constraints.NotBlank;

public class WarehouseRequest {
  @NotBlank
  private String name;
  private String code;
  private String location;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
