package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(
    name = "warehouse_locations",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"warehouse_id", "zone_name", "aisle_name", "rack_name", "shelf_name", "bin_name"}),
        @UniqueConstraint(columnNames = {"warehouse_id", "location_code"})
    }
)
public class WarehouseLocation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  @Column(name = "zone_name", nullable = false, length = 64)
  private String zone;

  @Column(name = "aisle_name", nullable = false, length = 64)
  private String aisle;

  @Column(name = "rack_name", nullable = false, length = 64)
  private String rack;

  @Column(name = "shelf_name", nullable = false, length = 64)
  private String shelf;

  @Column(name = "bin_name", nullable = false, length = 64)
  private String bin;

  @Column(name = "location_code", nullable = false, length = 350)
  private String locationCode;

  @Column(nullable = false, length = 128)
  private String tenantKey;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

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

  public String getLocationCode() {
    return locationCode;
  }

  public void setLocationCode(String locationCode) {
    this.locationCode = locationCode;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
