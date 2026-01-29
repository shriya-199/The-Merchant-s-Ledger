package com.merchantsledger.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String actorEmail;

  private Long actorUserId;

  @Column(nullable = false)
  private String entityType;

  private String entityId;

  @Column(length = 2000)
  private String metadata;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private String tenantKey;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getActorEmail() {
    return actorEmail;
  }

  public void setActorEmail(String actorEmail) {
    this.actorEmail = actorEmail;
  }

  public Long getActorUserId() {
    return actorUserId;
  }

  public void setActorUserId(Long actorUserId) {
    this.actorUserId = actorUserId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
  }
}
