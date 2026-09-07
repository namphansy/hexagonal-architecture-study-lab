package com.example.order.config;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalCreateOrderUseCase implements CreateOrderUseCase {

    private final CreateOrderUseCase delegate;

    public TransactionalCreateOrderUseCase(CreateOrderUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        return delegate.createOrder(command);
    }
}
