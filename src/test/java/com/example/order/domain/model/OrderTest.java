package com.example.order.domain.model;

import com.example.order.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void newOrderMustHaveAtLeastOneItem() {
        assertThrows(DomainException.class, () -> new Order("order-1", "customer-1", Collections.emptyList()));
    }

    @Test
    void itemQuantityMustBeGreaterThanZero() {
        assertThrows(DomainException.class, () -> new OrderItem("product-1", 0, BigDecimal.TEN));
    }

    @Test
    void itemPriceMustNotBeNegative() {
        assertThrows(DomainException.class, () -> new OrderItem("product-1", 1, BigDecimal.valueOf(-1)));
    }

    @Test
    void newOrderCalculatesTotalAmount() {
        Order order = new Order(
                "order-1",
                "customer-1",
                Arrays.asList(
                        new OrderItem("product-1", 2, BigDecimal.valueOf(10)),
                        new OrderItem("product-2", 3, BigDecimal.valueOf(5))
                )
        );

        assertEquals(0, BigDecimal.valueOf(35).compareTo(order.getTotalAmount()));
    }

    @Test
    void newOrderHasCreatedStatus() {
        Order order = new Order(
                "order-1",
                "customer-1",
                Collections.singletonList(new OrderItem("product-1", 1, BigDecimal.TEN))
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }
}
