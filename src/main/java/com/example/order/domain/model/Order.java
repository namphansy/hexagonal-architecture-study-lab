package com.example.order.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private String id;
    private String customerId;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private OrderStatus status;

    public Order() {
    }

    public Order(String id, String customerId, List<OrderItem> items, BigDecimal totalAmount, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        if (items != null) {
            this.items = new ArrayList<>(items);
        }
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
