package com.example.order.adapter.out.inventory;

import com.example.order.application.port.out.InventoryPort;

public class InventoryGrpcAdapter implements InventoryPort {

    @Override
    public boolean isAvailable(String productId, int quantity) {
        throw new UnsupportedOperationException("Inventory adapter exercise intentionally left blank.");
    }
}
