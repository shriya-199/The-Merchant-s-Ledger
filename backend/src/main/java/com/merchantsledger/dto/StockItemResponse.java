package com.merchantsledger.dto;

import java.time.Instant;

public class StockItemResponse {
  private Long id;
  private Long warehouseId;
  private String warehouseName;
  private Long productId;
  private String productName;
  private String sku;
  private long quantity;
  private Instant updatedAt;

  public StockItemResponse(Long id, Long warehouseId, String warehouseName,
                           Long productId, String productName, String sku,
                           long quantity, Instant updatedAt) {
    this.id = id;
    this.warehouseId = warehouseId;
    this.warehouseName = warehouseName;
    this.productId = productId;
    this.productName = productName;
    this.sku = sku;
    this.quantity = quantity;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public String getWarehouseName() {
    return warehouseName;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public String getSku() {
    return sku;
  }

  public long getQuantity() {
    return quantity;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
