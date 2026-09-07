package com.example.order.application.service;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.adapter.out.persistence.InMemoryOutboxRepositoryAdapter;
import com.example.order.adapter.out.persistence.InMemoryOrderRepositoryAdapter;
import com.example.order.application.exception.CustomerNotFoundException;
import com.example.order.application.exception.InsufficientInventoryException;
import com.example.order.application.port.out.CustomerQueryPort;
import com.example.order.application.port.out.InventoryPort;
import com.example.order.domain.exception.DomainException;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateOrderServiceTest {

    @Test
    void createOrderReturnsCreatedOrderResult() {
        InMemoryOrderRepositoryAdapter repository = new InMemoryOrderRepositoryAdapter();
        InMemoryOutboxRepositoryAdapter outboxRepository = new InMemoryOutboxRepositoryAdapter();
        CreateOrderService service = new CreateOrderService(
                customerExists(),
                inventoryAvailable(),
                repository,
                outboxRepository,
                () -> "order-1"
        );

        CreateOrderResult result = service.createOrder(new CreateOrderCommand(
                "customer-1",
                Arrays.asList(
                        new CreateOrderCommand.Item("product-1", 2, BigDecimal.valueOf(10)),
                        new CreateOrderCommand.Item("product-2", 1, BigDecimal.valueOf(5))
                )
        ));

        assertEquals("order-1", result.getOrderId());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(result.getTotalAmount()));
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertTrue(repository.findById("order-1").isPresent());
        assertEquals(1, outboxRepository.findUnpublished(100).size());
        assertEquals("order-1", outboxRepository.findUnpublished(100).get(0).getPayload().getOrderId());
    }

    @Test
    void createOrderUsesDomainValidation() {
        CreateOrderService service = new CreateOrderService(
                customerExists(),
                inventoryAvailable(),
                new InMemoryOrderRepositoryAdapter(),
                new InMemoryOutboxRepositoryAdapter(),
                () -> "order-1"
        );

        assertThrows(DomainException.class, () -> service.createOrder(new CreateOrderCommand(
                "customer-1",
                Collections.emptyList()
        )));
    }

    @Test
    void createOrderRejectsMissingCustomer() {
        InMemoryOrderRepositoryAdapter repository = new InMemoryOrderRepositoryAdapter();
        InMemoryOutboxRepositoryAdapter outboxRepository = new InMemoryOutboxRepositoryAdapter();
        CreateOrderService service = new CreateOrderService(
                customerMissing(),
                inventoryAvailable(),
                repository,
                outboxRepository,
                () -> "order-1"
        );

        assertThrows(CustomerNotFoundException.class, () -> service.createOrder(new CreateOrderCommand(
                "missing-customer",
                Collections.singletonList(new CreateOrderCommand.Item("product-1", 1, BigDecimal.TEN))
        )));
        assertTrue(outboxRepository.findUnpublished(100).isEmpty());
    }

    @Test
    void createOrderRejectsUnavailableInventory() {
        InMemoryOrderRepositoryAdapter repository = new InMemoryOrderRepositoryAdapter();
        InMemoryOutboxRepositoryAdapter outboxRepository = new InMemoryOutboxRepositoryAdapter();
        CreateOrderService service = new CreateOrderService(
                customerExists(),
                inventoryUnavailable(),
                repository,
                outboxRepository,
                () -> "order-1"
        );

        assertThrows(InsufficientInventoryException.class, () -> service.createOrder(new CreateOrderCommand(
                "customer-1",
                Collections.singletonList(new CreateOrderCommand.Item("product-1", 1, BigDecimal.TEN))
        )));
        assertFalse(repository.findById("order-1").isPresent());
        assertTrue(outboxRepository.findUnpublished(100).isEmpty());
    }

    private CustomerQueryPort customerExists() {
        return customerId -> true;
    }

    private CustomerQueryPort customerMissing() {
        return customerId -> false;
    }

    private InventoryPort inventoryAvailable() {
        return (productId, quantity) -> true;
    }

    private InventoryPort inventoryUnavailable() {
        return (productId, quantity) -> false;
    }
}
