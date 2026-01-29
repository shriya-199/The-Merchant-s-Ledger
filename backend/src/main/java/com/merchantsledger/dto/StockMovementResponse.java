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
  private String referenceNote;
  private Instant createdAt;

  public StockMovementResponse(Long id, MovementType type, Long productId, String productName,
                               Long fromWarehouseId, String fromWarehouseName,
                               Long toWarehouseId, String toWarehouseName,
                               long quantity, String referenceNote, Instant createdAt) {
    this.id = id;
    this.type = type;
    this.productId = productId;
    this.productName = productName;
    this.fromWarehouseId = fromWarehouseId;
    this.fromWarehouseName = fromWarehouseName;
    this.toWarehouseId = toWarehouseId;
    this.toWarehouseName = toWarehouseName;
    this.quantity = quantity;
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

  public String getReferenceNote() {
    return referenceNote;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
