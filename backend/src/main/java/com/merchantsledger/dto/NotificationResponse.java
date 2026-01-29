package com.merchantsledger.dto;

import java.time.Instant;

public class NotificationResponse {
  private Long id;
  private String title;
  private String message;
  private String severity;
  private Instant createdAt;
  private Instant readAt;

  public NotificationResponse(Long id, String title, String message, String severity,
                              Instant createdAt, Instant readAt) {
    this.id = id;
    this.title = title;
    this.message = message;
    this.severity = severity;
    this.createdAt = createdAt;
    this.readAt = readAt;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getSeverity() {
    return severity;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getReadAt() {
    return readAt;
  }
}
