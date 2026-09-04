package com.example.order.application.port.in;

import com.example.order.domain.model.OrderStatus;

import java.math.BigDecimal;

public class CreateOrderResult {

    private final String orderId;
    private final BigDecimal totalAmount;
    private final OrderStatus status;

    public CreateOrderResult(String orderId, BigDecimal totalAmount, OrderStatus status) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
