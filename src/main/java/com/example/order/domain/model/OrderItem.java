package com.example.order.domain.model;

import com.example.order.domain.exception.DomainException;

import java.math.BigDecimal;

public class OrderItem {

    private String productId;
    private int quantity;
    private BigDecimal price;

    public OrderItem() {
    }

    public OrderItem(String productId, int quantity, BigDecimal price) {
        if (quantity <= 0) {
            throw new DomainException("Order item quantity must be greater than zero.");
        }
        if (price == null) {
            throw new DomainException("Order item price is required.");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Order item price must not be negative.");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
