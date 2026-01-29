package com.merchantsledger.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductRequest {
  @NotBlank
  private String name;
  @NotBlank
  private String sku;
  private String barcode;
  private String category;
  private String unit;
  private long reorderLevel;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public long getReorderLevel() {
    return reorderLevel;
  }

  public void setReorderLevel(long reorderLevel) {
    this.reorderLevel = reorderLevel;
  }
}
