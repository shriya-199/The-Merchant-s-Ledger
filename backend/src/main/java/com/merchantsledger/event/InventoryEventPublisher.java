package com.merchantsledger.event;

import com.merchantsledger.dto.StockMovementResponse;

public interface InventoryEventPublisher {
  boolean publishMovementRecorded(String tenantKey, StockMovementResponse movement);
}
