package com.merchantsledger.dto;

import java.time.Instant;

import com.merchantsledger.entity.MovementType;

public class StockMovementResponse {
  private Long id;
  private MovementType type;
  private Long productId;
  private String productName;
  private Long fromWarehouseId;
  private String fromWarehouseName;
  private Long toWarehouseId;
  private String toWarehouseName;
  private long quantity;
  private String transactionId;
  private String idempotencyKey;
  private String correlationId;
  private String referenceType;
  private String referenceId;
  private String reasonCode;
  private String sourceLocation;
  private String destinationLocation;
  private String performedBy;
  private String performedVia;
  private String metadataJson;
  private String referenceNote;
  private Instant createdAt;

  public StockMovementResponse(Long id, MovementType type, Long productId, String productName,
                               Long fromWarehouseId, String fromWarehouseName,
                               Long toWarehouseId, String toWarehouseName,
                               long quantity, String transactionId, String idempotencyKey,
                               String correlationId, String referenceType, String referenceId,
                               String reasonCode, String sourceLocation, String destinationLocation,
                               String performedBy, String performedVia, String metadataJson,
                               String referenceNote, Instant createdAt) {
    this.id = id;
    this.type = type;
    this.productId = productId;
    this.productName = productName;
    this.fromWarehouseId = fromWarehouseId;
    this.fromWarehouseName = fromWarehouseName;
    this.toWarehouseId = toWarehouseId;
    this.toWarehouseName = toWarehouseName;
    this.quantity = quantity;
    this.transactionId = transactionId;
    this.idempotencyKey = idempotencyKey;
    this.correlationId = correlationId;
    this.referenceType = referenceType;
    this.referenceId = referenceId;
    this.reasonCode = reasonCode;
    this.sourceLocation = sourceLocation;
    this.destinationLocation = destinationLocation;
    this.performedBy = performedBy;
    this.performedVia = performedVia;
    this.metadataJson = metadataJson;
    this.referenceNote = referenceNote;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public MovementType getType() {
    return type;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public Long getFromWarehouseId() {
    return fromWarehouseId;
  }

  public String getFromWarehouseName() {
    return fromWarehouseName;
  }

  public Long getToWarehouseId() {
    return toWarehouseId;
  }

  public String getToWarehouseName() {
    return toWarehouseName;
  }

  public long getQuantity() {
    return quantity;
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

  public String getReasonCode() {
    return reasonCode;
  }

  public String getSourceLocation() {
    return sourceLocation;
  }

  public String getDestinationLocation() {
    return destinationLocation;
  }

  public String getPerformedBy() {
    return performedBy;
  }

  public String getPerformedVia() {
    return performedVia;
  }

  public String getMetadataJson() {
    return metadataJson;
  }

  public String getReferenceNote() {
    return referenceNote;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
