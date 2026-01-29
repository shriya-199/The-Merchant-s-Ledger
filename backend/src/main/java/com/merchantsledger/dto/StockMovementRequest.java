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

  public String getReferenceNote() {
    return referenceNote;
  }

  public void setReferenceNote(String referenceNote) {
    this.referenceNote = referenceNote;
  }
}
