package com.example.order.application.port.in;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateOrderCommand {

    private final String customerId;
    private final List<Item> items;

    public CreateOrderCommand(String customerId, List<Item> items) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static class Item {

        private final String productId;
        private final int quantity;
        private final BigDecimal price;

        public Item(String productId, int quantity, BigDecimal price) {
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
}
