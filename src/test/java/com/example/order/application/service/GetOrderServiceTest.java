package com.example.order.application.service;

import com.example.order.adapter.out.persistence.InMemoryOrderRepositoryAdapter;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetOrderServiceTest {

    @Test
    void getOrderReturnsOrderFromRepositoryPort() {
        InMemoryOrderRepositoryAdapter repository = new InMemoryOrderRepositoryAdapter();
        Order order = Order.create(
                "order-1",
                "customer-1",
                Collections.singletonList(new OrderItem("product-1", 1, BigDecimal.TEN))
        );
        repository.save(order);

        GetOrderService service = new GetOrderService(repository);

        assertTrue(service.getOrder("order-1").isPresent());
        assertEquals("customer-1", service.getOrder("order-1").get().getCustomerId());
    }

    @Test
    void getOrderReturnsEmptyWhenOrderDoesNotExist() {
        GetOrderService service = new GetOrderService(new InMemoryOrderRepositoryAdapter());

        assertFalse(service.getOrder("missing-order").isPresent());
    }
}
