package com.merchantsledger.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.eventing.rabbit.enabled", havingValue = "true")
public class RabbitMqConfig {
  @Bean
  public DirectExchange inventoryExchange(@Value("${app.eventing.rabbit.inventoryExchange}") String exchangeName) {
    return new DirectExchange(exchangeName, true, false);
  }

  @Bean
  public Queue inventoryQueue(@Value("${app.eventing.rabbit.inventoryQueue}") String queueName) {
    return new Queue(queueName, true);
  }

  @Bean
  public Binding inventoryBinding(
      Queue inventoryQueue,
      DirectExchange inventoryExchange,
      @Value("${app.eventing.rabbit.inventoryRoutingKey}") String routingKey) {
    return BindingBuilder.bind(inventoryQueue).to(inventoryExchange).with(routingKey);
  }

  @Bean
  public Jackson2JsonMessageConverter rabbitMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
