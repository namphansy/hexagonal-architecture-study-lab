package com.example.order.application.exception;

public class InsufficientInventoryException extends RuntimeException {

    public InsufficientInventoryException(String productId, int quantity) {
        super("Insufficient inventory for product " + productId + " and quantity " + quantity);
    }
}
