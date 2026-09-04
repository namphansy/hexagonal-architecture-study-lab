package com.example.order.application.port.in;

import com.example.order.domain.model.Order;

import java.util.Optional;

public interface GetOrderUseCase {

    Optional<Order> getOrder(String id);
}
