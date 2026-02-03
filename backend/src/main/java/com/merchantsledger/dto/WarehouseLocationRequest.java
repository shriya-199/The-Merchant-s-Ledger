package com.merchantsledger.dto;

import jakarta.validation.constraints.NotBlank;

public class WarehouseLocationRequest {
  @NotBlank
  private String zone;
  @NotBlank
  private String aisle;
  @NotBlank
  private String rack;
  @NotBlank
  private String shelf;
  @NotBlank
  private String bin;

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getAisle() {
    return aisle;
  }

  public void setAisle(String aisle) {
    this.aisle = aisle;
  }

  public String getRack() {
    return rack;
  }

  public void setRack(String rack) {
    this.rack = rack;
  }

  public String getShelf() {
    return shelf;
  }

  public void setShelf(String shelf) {
    this.shelf = shelf;
  }

  public String getBin() {
    return bin;
  }

  public void setBin(String bin) {
    this.bin = bin;
  }
}
