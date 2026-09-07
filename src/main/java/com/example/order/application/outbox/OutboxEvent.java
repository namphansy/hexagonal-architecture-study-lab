package com.example.order.application.outbox;

import com.example.order.application.event.OrderCreatedEvent;

import java.time.Instant;
import java.util.UUID;

public class OutboxEvent {

    public static final String ORDER_AGGREGATE_TYPE = "Order";
    public static final String ORDER_CREATED_EVENT_TYPE = "OrderCreated";

    private final String id;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final OrderCreatedEvent payload;
    private final Instant occurredAt;
    private final Instant publishedAt;

    public OutboxEvent(
            String id,
            String aggregateType,
            String aggregateId,
            String eventType,
            OrderCreatedEvent payload,
            Instant occurredAt,
            Instant publishedAt
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.publishedAt = publishedAt;
    }

    public static OutboxEvent orderCreated(OrderCreatedEvent event) {
        return new OutboxEvent(
                UUID.randomUUID().toString(),
                ORDER_AGGREGATE_TYPE,
                event.getOrderId(),
                ORDER_CREATED_EVENT_TYPE,
                event,
                event.getOccurredAt(),
                null
        );
    }

    public OutboxEvent markPublished(Instant publishedAt) {
        return new OutboxEvent(id, aggregateType, aggregateId, eventType, payload, occurredAt, publishedAt);
    }

    public String getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public OrderCreatedEvent getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
