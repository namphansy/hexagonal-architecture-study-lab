package com.example.order.application.port.in;

public class CreateOrderResult {

    private final String orderId;

    public CreateOrderResult(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
