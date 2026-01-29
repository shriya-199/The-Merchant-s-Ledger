package com.merchantsledger.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class CustomerResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private String address;
  private BigDecimal balance;
  private Instant createdAt;

  public CustomerResponse(Long id, String name, String email, String phone, String address, BigDecimal balance, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.balance = balance;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
