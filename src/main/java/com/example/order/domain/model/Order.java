package com.example.order.domain.model;

import com.example.order.domain.exception.DomainException;

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

    public Order(String id, String customerId, List<OrderItem> items) {
        this.id = id;
        this.customerId = customerId;
        setItems(items);
        this.status = OrderStatus.CREATED;
        this.totalAmount = calculateTotal();
    }

    public Order(String id, String customerId, List<OrderItem> items, BigDecimal totalAmount, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        setItems(items);
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public static Order create(String id, String customerId, List<OrderItem> items) {
        return new Order(id, customerId, items);
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private void setItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one item.");
        }
        this.items = new ArrayList<>(items);
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
