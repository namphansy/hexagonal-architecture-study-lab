package com.example.order.adapter.in.web;

public class CreateOrderResponse {

    private final String orderId;

    public CreateOrderResponse(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
