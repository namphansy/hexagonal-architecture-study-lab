package com.example.order.application.service;

import com.example.order.adapter.out.persistence.InMemoryOutboxRepositoryAdapter;
import com.example.order.application.event.OrderCreatedEvent;
import com.example.order.application.outbox.OutboxEvent;
import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishOutboxEventsServiceTest {

    @Test
    void publishPendingEventsPublishesAndMarksEventAsPublished() {
        InMemoryOutboxRepositoryAdapter outboxRepository = new InMemoryOutboxRepositoryAdapter();
        CapturingOrderEventPublisher publisher = new CapturingOrderEventPublisher();
        PublishOutboxEventsService service = new PublishOutboxEventsService(outboxRepository, publisher);

        outboxRepository.save(OutboxEvent.orderCreated(new OrderCreatedEvent(
                "order-1",
                "customer-1",
                BigDecimal.TEN,
                OrderStatus.CREATED,
                Instant.parse("2026-09-07T00:00:00Z")
        )));

        service.publishPendingEvents();

        assertTrue(publisher.lastEvent().isPresent());
        assertEquals("order-1", publisher.lastEvent().get().getOrderId());
        assertTrue(outboxRepository.findUnpublished(100).isEmpty());
    }

    @Test
    void publishPendingEventsLeavesEventUnpublishedWhenPublisherFails() {
        InMemoryOutboxRepositoryAdapter outboxRepository = new InMemoryOutboxRepositoryAdapter();
        PublishOutboxEventsService service = new PublishOutboxEventsService(
                outboxRepository,
                event -> {
                    throw new RuntimeException("Kafka unavailable");
                }
        );

        outboxRepository.save(OutboxEvent.orderCreated(new OrderCreatedEvent(
                "order-1",
                "customer-1",
                BigDecimal.TEN,
                OrderStatus.CREATED,
                Instant.parse("2026-09-07T00:00:00Z")
        )));

        assertThrows(RuntimeException.class, service::publishPendingEvents);

        assertFalse(outboxRepository.findUnpublished(100).isEmpty());
    }

    private static class CapturingOrderEventPublisher implements OrderEventPublisherPort {

        private OrderCreatedEvent lastEvent;

        @Override
        public void publishOrderCreated(OrderCreatedEvent event) {
            this.lastEvent = event;
        }

        Optional<OrderCreatedEvent> lastEvent() {
            return Optional.ofNullable(lastEvent);
        }
    }
}
