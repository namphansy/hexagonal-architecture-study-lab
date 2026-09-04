package com.example.order.application.service;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.exception.CustomerNotFoundException;
import com.example.order.application.port.out.CustomerQueryPort;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CreateOrderService implements CreateOrderUseCase {

    private final CustomerQueryPort customerQueryPort;
    private final OrderRepositoryPort orderRepository;
    private final Supplier<String> orderIdGenerator;

    public CreateOrderService(CustomerQueryPort customerQueryPort, OrderRepositoryPort orderRepository) {
        this(customerQueryPort, orderRepository, () -> UUID.randomUUID().toString());
    }

    public CreateOrderService(
            CustomerQueryPort customerQueryPort,
            OrderRepositoryPort orderRepository,
            Supplier<String> orderIdGenerator
    ) {
        this.customerQueryPort = customerQueryPort;
        this.orderRepository = orderRepository;
        this.orderIdGenerator = orderIdGenerator;
    }

    @Override
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create order command is required.");
        }
        if (!customerQueryPort.existsById(command.getCustomerId())) {
            throw new CustomerNotFoundException(command.getCustomerId());
        }

        Order order = Order.create(
                orderIdGenerator.get(),
                command.getCustomerId(),
                toDomainItems(command.getItems())
        );
        Order savedOrder = orderRepository.save(order);

        return new CreateOrderResult(savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getStatus());
    }

    private List<OrderItem> toDomainItems(List<CreateOrderCommand.Item> items) {
        List<OrderItem> domainItems = new ArrayList<>();
        for (CreateOrderCommand.Item item : items) {
            domainItems.add(new OrderItem(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return domainItems;
    }
}
