package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "cycle_count_lines")
public class CycleCountLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "session_id")
  private CycleCountSession session;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private long expectedQty;

  @Column(nullable = false)
  private long countedQty;

  @Column(nullable = false)
  private long variance;

  private String sourceLocation;
  private String reasonCode;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public CycleCountSession getSession() {
    return session;
  }

  public void setSession(CycleCountSession session) {
    this.session = session;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public long getExpectedQty() {
    return expectedQty;
  }

  public void setExpectedQty(long expectedQty) {
    this.expectedQty = expectedQty;
  }

  public long getCountedQty() {
    return countedQty;
  }

  public void setCountedQty(long countedQty) {
    this.countedQty = countedQty;
  }

  public long getVariance() {
    return variance;
  }

  public void setVariance(long variance) {
    this.variance = variance;
  }

  public String getSourceLocation() {
    return sourceLocation;
  }

  public void setSourceLocation(String sourceLocation) {
    this.sourceLocation = sourceLocation;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
