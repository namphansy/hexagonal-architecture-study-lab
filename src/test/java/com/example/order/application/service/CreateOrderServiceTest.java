package com.example.order.application.service;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.domain.exception.DomainException;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateOrderServiceTest {

    @Test
    void createOrderReturnsCreatedOrderResult() {
        CreateOrderService service = new CreateOrderService(() -> "order-1");

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
    }

    @Test
    void createOrderUsesDomainValidation() {
        CreateOrderService service = new CreateOrderService(() -> "order-1");

        assertThrows(DomainException.class, () -> service.createOrder(new CreateOrderCommand(
                "customer-1",
                Collections.emptyList()
        )));
    }
}
