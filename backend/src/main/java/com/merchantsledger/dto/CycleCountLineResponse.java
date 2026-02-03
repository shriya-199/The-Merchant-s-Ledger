package com.merchantsledger.dto;

import java.time.Instant;

public class CycleCountLineResponse {
  private Long id;
  private Long productId;
  private String productName;
  private String sku;
  private long expectedQty;
  private long countedQty;
  private long variance;
  private String sourceLocation;
  private String reasonCode;
  private Instant createdAt;

  public CycleCountLineResponse(Long id, Long productId, String productName, String sku,
                                long expectedQty, long countedQty, long variance,
                                String sourceLocation, String reasonCode, Instant createdAt) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.sku = sku;
    this.expectedQty = expectedQty;
    this.countedQty = countedQty;
    this.variance = variance;
    this.sourceLocation = sourceLocation;
    this.reasonCode = reasonCode;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public String getSku() {
    return sku;
  }

  public long getExpectedQty() {
    return expectedQty;
  }

  public long getCountedQty() {
    return countedQty;
  }

  public long getVariance() {
    return variance;
  }

  public String getSourceLocation() {
    return sourceLocation;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
