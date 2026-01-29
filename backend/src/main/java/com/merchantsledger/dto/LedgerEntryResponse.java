package com.merchantsledger.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.merchantsledger.entity.LedgerType;

public class LedgerEntryResponse {
  private Long id;
  private Long customerId;
  private String customerName;
  private LedgerType type;
  private BigDecimal amount;
  private String description;
  private Instant createdAt;

  public LedgerEntryResponse(Long id, Long customerId, String customerName, LedgerType type,
                             BigDecimal amount, String description, Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.customerName = customerName;
    this.type = type;
    this.amount = amount;
    this.description = description;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public LedgerType getType() {
    return type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getDescription() {
    return description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
