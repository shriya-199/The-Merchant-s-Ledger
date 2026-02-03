package com.merchantsledger.dto;

import java.time.Instant;
import java.util.List;

import com.merchantsledger.entity.CycleCountStatus;

public class CycleCountSessionResponse {
  private Long id;
  private String name;
  private Long warehouseId;
  private String warehouseName;
  private CycleCountStatus status;
  private String createdBy;
  private String submittedBy;
  private String approvedBy;
  private Instant createdAt;
  private Instant submittedAt;
  private Instant approvedAt;
  private List<CycleCountLineResponse> lines;

  public CycleCountSessionResponse(Long id, String name, Long warehouseId, String warehouseName,
                                   CycleCountStatus status, String createdBy, String submittedBy, String approvedBy,
                                   Instant createdAt, Instant submittedAt, Instant approvedAt,
                                   List<CycleCountLineResponse> lines) {
    this.id = id;
    this.name = name;
    this.warehouseId = warehouseId;
    this.warehouseName = warehouseName;
    this.status = status;
    this.createdBy = createdBy;
    this.submittedBy = submittedBy;
    this.approvedBy = approvedBy;
    this.createdAt = createdAt;
    this.submittedAt = submittedAt;
    this.approvedAt = approvedAt;
    this.lines = lines;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public String getWarehouseName() {
    return warehouseName;
  }

  public CycleCountStatus getStatus() {
    return status;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public String getApprovedBy() {
    return approvedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getSubmittedAt() {
    return submittedAt;
  }

  public Instant getApprovedAt() {
    return approvedAt;
  }

  public List<CycleCountLineResponse> getLines() {
    return lines;
  }
}
