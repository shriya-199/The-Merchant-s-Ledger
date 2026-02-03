package com.merchantsledger.dto;

import java.time.Instant;

public class WarehouseLocationResponse {
  private Long id;
  private Long warehouseId;
  private String warehouseName;
  private String zone;
  private String aisle;
  private String rack;
  private String shelf;
  private String bin;
  private String locationCode;
  private Instant createdAt;

  public WarehouseLocationResponse(Long id, Long warehouseId, String warehouseName, String zone, String aisle, String rack,
                                   String shelf, String bin, String locationCode, Instant createdAt) {
    this.id = id;
    this.warehouseId = warehouseId;
    this.warehouseName = warehouseName;
    this.zone = zone;
    this.aisle = aisle;
    this.rack = rack;
    this.shelf = shelf;
    this.bin = bin;
    this.locationCode = locationCode;
    this.createdAt = createdAt;
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

  public String getZone() {
    return zone;
  }

  public String getAisle() {
    return aisle;
  }

  public String getRack() {
    return rack;
  }

  public String getShelf() {
    return shelf;
  }

  public String getBin() {
    return bin;
  }

  public String getLocationCode() {
    return locationCode;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
