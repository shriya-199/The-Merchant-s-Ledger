package com.merchantsledger.dto;

import java.time.Instant;

public class ProductResponse {
  private Long id;
  private String name;
  private String sku;
  private String barcode;
  private String category;
  private String unit;
  private long reorderLevel;
  private Instant createdAt;

  public ProductResponse(Long id, String name, String sku, String barcode,
                         String category, String unit, long reorderLevel, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.sku = sku;
    this.barcode = barcode;
    this.category = category;
    this.unit = unit;
    this.reorderLevel = reorderLevel;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSku() {
    return sku;
  }

  public String getBarcode() {
    return barcode;
  }

  public String getCategory() {
    return category;
  }

  public String getUnit() {
    return unit;
  }

  public long getReorderLevel() {
    return reorderLevel;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
