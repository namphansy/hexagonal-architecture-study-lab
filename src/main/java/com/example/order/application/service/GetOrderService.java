package com.example.order.application.service;

import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.domain.model.Order;

import java.util.Optional;

public class GetOrderService implements GetOrderUseCase {

    @Override
    public Optional<Order> getOrder(String id) {
        throw new UnsupportedOperationException("Exercise implementation intentionally left blank.");
    }
}
