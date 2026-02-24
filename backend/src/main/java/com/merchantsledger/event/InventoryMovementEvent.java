package com.merchantsledger.event;

import com.merchantsledger.dto.StockMovementResponse;

public class InventoryMovementEvent {
  private String tenantKey;
  private StockMovementResponse movement;

  public InventoryMovementEvent() {
  }

  public InventoryMovementEvent(String tenantKey, StockMovementResponse movement) {
    this.tenantKey = tenantKey;
    this.movement = movement;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
  }

  public StockMovementResponse getMovement() {
    return movement;
  }

  public void setMovement(StockMovementResponse movement) {
    this.movement = movement;
  }
}
