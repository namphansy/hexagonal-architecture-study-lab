package com.example.order.application.service;

import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;

import java.util.Optional;

public class GetOrderService implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public GetOrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Order> getOrder(String id) {
        return orderRepository.findById(id);
    }
}
