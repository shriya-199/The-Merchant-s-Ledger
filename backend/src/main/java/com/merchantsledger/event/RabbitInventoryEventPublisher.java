package com.merchantsledger.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.merchantsledger.dto.StockMovementResponse;

@Component
@ConditionalOnProperty(name = "app.eventing.rabbit.enabled", havingValue = "true")
public class RabbitInventoryEventPublisher implements InventoryEventPublisher {
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingKey;

  public RabbitInventoryEventPublisher(
      RabbitTemplate rabbitTemplate,
      @Value("${app.eventing.rabbit.inventoryExchange}") String exchange,
      @Value("${app.eventing.rabbit.inventoryRoutingKey}") String routingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  @Override
  public boolean publishMovementRecorded(String tenantKey, StockMovementResponse movement) {
    rabbitTemplate.convertAndSend(exchange, routingKey, new InventoryMovementEvent(tenantKey, movement));
    return true;
  }
}
