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

  @Column(length = 64)
  private String transactionId;

  @Column(length = 128)
  private String idempotencyKey;

  @Column(length = 128)
  private String correlationId;

  @Column(length = 32)
  private String referenceType;

  @Column(length = 64)
  private String referenceId;

  @Column(length = 64)
  private String reasonCode;

  @Column(length = 128)
  private String sourceLocation;

  @Column(length = 128)
  private String destinationLocation;

  @Column(length = 128)
  private String performedBy;

  @Column(length = 32)
  private String performedVia;

  @Column(columnDefinition = "TEXT")
  private String metadataJson;

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

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public String getSourceLocation() {
    return sourceLocation;
  }

  public void setSourceLocation(String sourceLocation) {
    this.sourceLocation = sourceLocation;
  }

  public String getDestinationLocation() {
    return destinationLocation;
  }

  public void setDestinationLocation(String destinationLocation) {
    this.destinationLocation = destinationLocation;
  }

  public String getPerformedBy() {
    return performedBy;
  }

  public void setPerformedBy(String performedBy) {
    this.performedBy = performedBy;
  }

  public String getPerformedVia() {
    return performedVia;
  }

  public void setPerformedVia(String performedVia) {
    this.performedVia = performedVia;
  }

  public String getMetadataJson() {
    return metadataJson;
  }

  public void setMetadataJson(String metadataJson) {
    this.metadataJson = metadataJson;
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
