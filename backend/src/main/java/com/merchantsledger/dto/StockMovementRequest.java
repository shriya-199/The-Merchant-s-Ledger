package com.merchantsledger.dto;

import com.merchantsledger.entity.MovementType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockMovementRequest {
  @NotNull
  private MovementType type;

  @NotNull
  private Long productId;

  private Long fromWarehouseId;
  private Long toWarehouseId;

  @Min(1)
  private long quantity;

  private String adjustmentDirection;
  private String transactionId;
  private String idempotencyKey;
  private String correlationId;
  private String referenceType;
  private String referenceId;
  private String reasonCode;
  private String sourceLocation;
  private String destinationLocation;
  private String performedVia;
  private String metadataJson;
  private String referenceNote;

  public MovementType getType() {
    return type;
  }

  public void setType(MovementType type) {
    this.type = type;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getFromWarehouseId() {
    return fromWarehouseId;
  }

  public void setFromWarehouseId(Long fromWarehouseId) {
    this.fromWarehouseId = fromWarehouseId;
  }

  public Long getToWarehouseId() {
    return toWarehouseId;
  }

  public void setToWarehouseId(Long toWarehouseId) {
    this.toWarehouseId = toWarehouseId;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public String getAdjustmentDirection() {
    return adjustmentDirection;
  }

  public void setAdjustmentDirection(String adjustmentDirection) {
    this.adjustmentDirection = adjustmentDirection;
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

  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public String getSourceLocation() {
    return sourceLocation;
  }

  public void setSourceLocation(String sourceLocation) {
    this.sourceLocation = sourceLocation;
  }

  public String getDestinationLocation() {
    return destinationLocation;
  }

  public void setDestinationLocation(String destinationLocation) {
    this.destinationLocation = destinationLocation;
  }

  public String getPerformedVia() {
    return performedVia;
  }

  public void setPerformedVia(String performedVia) {
    this.performedVia = performedVia;
  }

  public String getMetadataJson() {
    return metadataJson;
  }

  public void setMetadataJson(String metadataJson) {
    this.metadataJson = metadataJson;
  }

  public String getReferenceNote() {
    return referenceNote;
  }

  public void setReferenceNote(String referenceNote) {
    this.referenceNote = referenceNote;
  }
}
