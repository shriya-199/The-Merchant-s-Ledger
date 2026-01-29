package com.merchantsledger.dto;

import java.time.Instant;

public class AuditLogResponse {
  private Long id;
  private String action;
  private String actorEmail;
  private String entityType;
  private String entityId;
  private String metadata;
  private Instant createdAt;

  public AuditLogResponse(Long id, String action, String actorEmail, String entityType,
                          String entityId, String metadata, Instant createdAt) {
    this.id = id;
    this.action = action;
    this.actorEmail = actorEmail;
    this.entityType = entityType;
    this.entityId = entityId;
    this.metadata = metadata;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getAction() {
    return action;
  }

  public String getActorEmail() {
    return actorEmail;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getMetadata() {
    return metadata;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
