package com.example.order.application.service;

import com.example.order.application.event.OrderCreatedEvent;
import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.exception.CustomerNotFoundException;
import com.example.order.application.exception.InsufficientInventoryException;
import com.example.order.application.port.out.CustomerQueryPort;
import com.example.order.application.port.out.InventoryPort;
import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CreateOrderService implements CreateOrderUseCase {

    private final CustomerQueryPort customerQueryPort;
    private final InventoryPort inventoryPort;
    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort orderEventPublisher;
    private final Supplier<String> orderIdGenerator;

    public CreateOrderService(
            CustomerQueryPort customerQueryPort,
            InventoryPort inventoryPort,
            OrderRepositoryPort orderRepository,
            OrderEventPublisherPort orderEventPublisher
    ) {
        this(customerQueryPort, inventoryPort, orderRepository, orderEventPublisher, () -> UUID.randomUUID().toString());
    }

    public CreateOrderService(
            CustomerQueryPort customerQueryPort,
            InventoryPort inventoryPort,
            OrderRepositoryPort orderRepository,
            OrderEventPublisherPort orderEventPublisher,
            Supplier<String> orderIdGenerator
    ) {
        this.customerQueryPort = customerQueryPort;
        this.inventoryPort = inventoryPort;
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
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
        List<OrderItem> domainItems = toDomainItems(command.getItems());
        ensureInventoryAvailable(domainItems);

        Order order = Order.create(
                orderIdGenerator.get(),
                command.getCustomerId(),
                domainItems
        );
        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(OrderCreatedEvent.from(savedOrder));

        return new CreateOrderResult(savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getStatus());
    }

    private void ensureInventoryAvailable(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (!inventoryPort.isAvailable(item.getProductId(), item.getQuantity())) {
                throw new InsufficientInventoryException(item.getProductId(), item.getQuantity());
            }
        }
    }

    private List<OrderItem> toDomainItems(List<CreateOrderCommand.Item> items) {
        List<OrderItem> domainItems = new ArrayList<>();
        for (CreateOrderCommand.Item item : items) {
            domainItems.add(new OrderItem(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return domainItems;
    }
}
