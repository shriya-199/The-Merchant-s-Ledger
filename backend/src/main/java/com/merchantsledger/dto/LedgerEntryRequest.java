package com.merchantsledger.dto;

import java.math.BigDecimal;

import com.merchantsledger.entity.LedgerType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LedgerEntryRequest {
  @NotNull
  private Long customerId;

  @NotNull
  private LedgerType type;

  @NotNull
  @Positive
  private BigDecimal amount;

  private String description;

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public LedgerType getType() {
    return type;
  }

  public void setType(LedgerType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
