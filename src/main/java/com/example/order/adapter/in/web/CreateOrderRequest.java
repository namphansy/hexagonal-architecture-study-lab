package com.example.order.adapter.in.web;

import com.example.order.domain.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {

    private String customerId;
    private List<OrderItem> items = new ArrayList<>();

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
