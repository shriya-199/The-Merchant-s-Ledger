package com.merchantsledger.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.merchantsledger.dto.StockMovementResponse;

@Component
@ConditionalOnProperty(name = "app.eventing.rabbit.enabled", havingValue = "false", matchIfMissing = true)
public class NoopInventoryEventPublisher implements InventoryEventPublisher {
  @Override
  public boolean publishMovementRecorded(String tenantKey, StockMovementResponse movement) {
    return false;
  }
}
