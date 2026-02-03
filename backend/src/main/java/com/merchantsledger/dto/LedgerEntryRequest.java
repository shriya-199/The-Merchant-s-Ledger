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

  private String transactionId;
  private String idempotencyKey;
  private String correlationId;
  private String referenceType;
  private String referenceId;
  private Long relatedMovementId;
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

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public Long getRelatedMovementId() {
    return relatedMovementId;
  }

  public void setRelatedMovementId(Long relatedMovementId) {
    this.relatedMovementId = relatedMovementId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
