package com.merchantsledger.exception;

import java.time.Instant;

public class ApiError {
  private final String message;
  private final String path;
  private final int status;
  private final Instant timestamp;

  public ApiError(String message, String path, int status) {
    this.message = message;
    this.path = path;
    this.status = status;
    this.timestamp = Instant.now();
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public int getStatus() {
    return status;
  }

  public Instant getTimestamp() {
    return timestamp;
  }
}
