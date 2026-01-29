package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"warehouse_id", "product_id"})
})
public class StockItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private long quantity;
  @Column(nullable = false)
  private String tenantKey;

  @Column(nullable = false, updatable = false)
  private Instant updatedAt;

  @PrePersist
  @PreUpdate
  public void onUpdate() {
    updatedAt = Instant.now();
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

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
