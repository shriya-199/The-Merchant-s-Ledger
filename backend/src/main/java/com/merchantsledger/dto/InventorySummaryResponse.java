package com.merchantsledger.dto;

public class InventorySummaryResponse {
  private long warehouseCount;
  private long productCount;
  private long totalUnits;

  public InventorySummaryResponse(long warehouseCount, long productCount, long totalUnits) {
    this.warehouseCount = warehouseCount;
    this.productCount = productCount;
    this.totalUnits = totalUnits;
  }

  public long getWarehouseCount() {
    return warehouseCount;
  }

  public long getProductCount() {
    return productCount;
  }

  public long getTotalUnits() {
    return totalUnits;
  }
}
