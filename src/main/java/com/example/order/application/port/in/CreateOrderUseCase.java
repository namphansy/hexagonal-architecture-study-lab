package com.example.order.application.port.in;

public interface CreateOrderUseCase {

    CreateOrderResult createOrder(CreateOrderCommand command);
}
