package com.merchantsledger.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CycleCountLineRequest {
  @NotNull
  private Long productId;

  @Min(0)
  private long countedQty;

  private String sourceLocation;
  private String reasonCode;

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public long getCountedQty() {
    return countedQty;
  }

  public void setCountedQty(long countedQty) {
    this.countedQty = countedQty;
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
}
