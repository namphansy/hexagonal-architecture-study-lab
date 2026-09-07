package com.example.order.adapter.in.messaging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderMessage {

    private String customerId;
    private List<ItemMessage> items = new ArrayList<>();

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<ItemMessage> getItems() {
        return items;
    }

    public void setItems(List<ItemMessage> items) {
        this.items = items;
    }

    public static class ItemMessage {

        private String productId;
        private int quantity;
        private BigDecimal price;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
