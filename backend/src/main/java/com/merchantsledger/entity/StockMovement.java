package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MovementType type;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "from_warehouse_id")
  private Warehouse fromWarehouse;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "to_warehouse_id")
  private Warehouse toWarehouse;

  @Column(nullable = false)
  private long quantity;
  @Column(nullable = false)
  private String tenantKey;

  private String referenceNote;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public MovementType getType() {
    return type;
  }

  public void setType(MovementType type) {
    this.type = type;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Warehouse getFromWarehouse() {
    return fromWarehouse;
  }

  public void setFromWarehouse(Warehouse fromWarehouse) {
    this.fromWarehouse = fromWarehouse;
  }

  public Warehouse getToWarehouse() {
    return toWarehouse;
  }

  public void setToWarehouse(Warehouse toWarehouse) {
    this.toWarehouse = toWarehouse;
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

  public String getReferenceNote() {
    return referenceNote;
  }

  public void setReferenceNote(String referenceNote) {
    this.referenceNote = referenceNote;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
