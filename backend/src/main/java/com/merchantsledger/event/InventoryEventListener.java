package com.merchantsledger.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.eventing.rabbit.enabled", havingValue = "true")
public class InventoryEventListener {
  private final SimpMessagingTemplate messagingTemplate;

  public InventoryEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @RabbitListener(queues = "${app.eventing.rabbit.inventoryQueue}")
  public void onMovementRecorded(InventoryMovementEvent event) {
    if (event == null || event.getMovement() == null) {
      return;
    }
    messagingTemplate.convertAndSend("/topic/stock", event.getMovement());
  }
}
