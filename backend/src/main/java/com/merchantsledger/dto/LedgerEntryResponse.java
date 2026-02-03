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
  private String transactionId;
  private String idempotencyKey;
  private String correlationId;
  private String referenceType;
  private String referenceId;
  private Long relatedMovementId;
  private String description;
  private Instant createdAt;

  public LedgerEntryResponse(Long id, Long customerId, String customerName, LedgerType type,
                             BigDecimal amount, String transactionId, String idempotencyKey,
                             String correlationId, String referenceType, String referenceId,
                             Long relatedMovementId, String description, Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.customerName = customerName;
    this.type = type;
    this.amount = amount;
    this.transactionId = transactionId;
    this.idempotencyKey = idempotencyKey;
    this.correlationId = correlationId;
    this.referenceType = referenceType;
    this.referenceId = referenceId;
    this.relatedMovementId = relatedMovementId;
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

  public String getTransactionId() {
    return transactionId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public Long getRelatedMovementId() {
    return relatedMovementId;
  }

  public String getDescription() {
    return description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
