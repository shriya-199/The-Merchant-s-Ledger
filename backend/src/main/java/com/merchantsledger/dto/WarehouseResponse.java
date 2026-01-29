package com.merchantsledger.dto;

import java.time.Instant;

public class WarehouseResponse {
  private Long id;
  private String name;
  private String code;
  private String location;
  private Instant createdAt;

  public WarehouseResponse(Long id, String name, String code, String location, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.code = code;
    this.location = location;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public String getLocation() {
    return location;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
