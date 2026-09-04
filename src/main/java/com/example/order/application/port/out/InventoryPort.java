package com.example.order.application.port.out;

public interface InventoryPort {

    boolean isAvailable(String productId, int quantity);
}
