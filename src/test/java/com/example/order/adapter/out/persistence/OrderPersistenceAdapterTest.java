package com.example.order.adapter.out.persistence;

import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OrderPersistenceAdapterTest {

    @Autowired
    private SpringDataOrderRepository repository;

    @Test
    void savesAndFindsOrderUsingJpaRepository() {
        OrderPersistenceAdapter adapter = new OrderPersistenceAdapter(repository);
        Order order = Order.create(
                "order-1",
                "customer-1",
                Collections.singletonList(new OrderItem("product-1", 2, BigDecimal.TEN))
        );

        adapter.save(order);
        Optional<Order> foundOrder = adapter.findById("order-1");

        assertTrue(foundOrder.isPresent());
        assertEquals("customer-1", foundOrder.get().getCustomerId());
        assertEquals(1, foundOrder.get().getItems().size());
    }
}
