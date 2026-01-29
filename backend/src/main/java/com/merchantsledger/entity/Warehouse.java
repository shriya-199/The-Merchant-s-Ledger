package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String code;
  private String location;
  @Column(nullable = false)
  private String tenantKey;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

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

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
