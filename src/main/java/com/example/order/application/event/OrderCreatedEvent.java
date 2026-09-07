package com.example.order.application.event;

import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderCreatedEvent {

    private final String orderId;
    private final String customerId;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final Instant occurredAt;

    public OrderCreatedEvent(
            String orderId,
            String customerId,
            BigDecimal totalAmount,
            OrderStatus status,
            Instant occurredAt
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                Instant.now()
        );
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
