package com.example.order.adapter.out.persistence;

import com.example.order.application.event.OrderCreatedEvent;
import com.example.order.application.outbox.OutboxEvent;
import com.example.order.domain.model.OrderStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

class OutboxPersistenceMapper {

    private final ObjectMapper objectMapper;

    OutboxPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OutboxJpaEntity toEntity(OutboxEvent event) {
        OutboxJpaEntity entity = new OutboxJpaEntity();
        entity.setId(event.getId());
        entity.setAggregateType(event.getAggregateType());
        entity.setAggregateId(event.getAggregateId());
        entity.setEventType(event.getEventType());
        entity.setPayload(toJson(event.getPayload()));
        entity.setOccurredAt(event.getOccurredAt());
        entity.setPublishedAt(event.getPublishedAt());
        return entity;
    }

    OutboxEvent toDomain(OutboxJpaEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                toOrderCreatedEvent(entity.getPayload()),
                entity.getOccurredAt(),
                entity.getPublishedAt()
        );
    }

    private String toJson(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (IOException exception) {
            throw new OutboxPersistenceException("Failed to serialize outbox event payload.", exception);
        }
    }

    private OrderCreatedEvent toOrderCreatedEvent(String payload) {
        try {
            JsonNode json = objectMapper.readTree(payload);
            return new OrderCreatedEvent(
                    json.get("orderId").asText(),
                    json.get("customerId").asText(),
                    new BigDecimal(json.get("totalAmount").asText()),
                    OrderStatus.valueOf(json.get("status").asText()),
                    Instant.parse(json.get("occurredAt").asText())
            );
        } catch (IOException exception) {
            throw new OutboxPersistenceException("Failed to deserialize outbox event payload.", exception);
        }
    }
}
