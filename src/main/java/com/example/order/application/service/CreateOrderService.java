package com.example.order.application.service;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;

public class CreateOrderService implements CreateOrderUseCase {

    @Override
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        throw new UnsupportedOperationException("Exercise implementation intentionally left blank.");
    }
}
