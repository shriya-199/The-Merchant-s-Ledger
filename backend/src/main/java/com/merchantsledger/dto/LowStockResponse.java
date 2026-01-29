package com.merchantsledger.dto;

public class LowStockResponse {
  private Long warehouseId;
  private String warehouseName;
  private Long productId;
  private String productName;
  private String sku;
  private long quantity;
  private long reorderLevel;

  public LowStockResponse(Long warehouseId, String warehouseName, Long productId, String productName,
                          String sku, long quantity, long reorderLevel) {
    this.warehouseId = warehouseId;
    this.warehouseName = warehouseName;
    this.productId = productId;
    this.productName = productName;
    this.sku = sku;
    this.quantity = quantity;
    this.reorderLevel = reorderLevel;
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

  public long getReorderLevel() {
    return reorderLevel;
  }
}
