package com.example.order.adapter.out.messaging;

import com.example.order.application.event.OrderCreatedEvent;
import com.example.order.application.port.out.OrderEventPublisherPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventAdapter implements OrderEventPublisherPort {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String orderCreatedTopic;

    public KafkaOrderEventAdapter(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            @Value("${messaging.kafka.topics.order-created}") String orderCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedTopic = orderCreatedTopic;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(orderCreatedTopic, event.getOrderId(), event);
    }
}
