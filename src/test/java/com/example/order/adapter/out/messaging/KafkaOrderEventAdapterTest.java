package com.example.order.adapter.out.messaging;

import com.example.order.application.event.OrderCreatedEvent;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaOrderEventAdapterTest {

    @Test
    void publishOrderCreatedSendsEventToConfiguredTopic() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaOrderEventAdapter adapter = new KafkaOrderEventAdapter(kafkaTemplate, "order-created");
        OrderCreatedEvent event = new OrderCreatedEvent(
                "order-1",
                "customer-1",
                BigDecimal.TEN,
                OrderStatus.CREATED,
                Instant.parse("2026-09-07T00:00:00Z")
        );

        adapter.publishOrderCreated(event);

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(kafkaTemplate).send("order-created", "order-1", eventCaptor.capture());
        assertEquals("customer-1", eventCaptor.getValue().getCustomerId());
    }
}
