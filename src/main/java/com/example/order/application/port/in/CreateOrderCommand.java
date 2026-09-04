package com.example.order.application.port.in;

import com.example.order.domain.model.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateOrderCommand {

    private String customerId;
    private List<OrderItem> items = new ArrayList<>();

    public CreateOrderCommand() {
    }

    public CreateOrderCommand(String customerId, List<OrderItem> items) {
        this.customerId = customerId;
        if (items != null) {
            this.items = new ArrayList<>(items);
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
