package com.example.order.adapter.out.persistence;

import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderPersistenceMapperTest {

    private final OrderPersistenceMapper mapper = new OrderPersistenceMapper();

    @Test
    void mapsDomainOrderToJpaEntity() {
        Order order = Order.create(
                "order-1",
                "customer-1",
                Arrays.asList(
                        new OrderItem("product-1", 2, BigDecimal.TEN),
                        new OrderItem("product-2", 1, BigDecimal.valueOf(5))
                )
        );

        OrderJpaEntity entity = mapper.toEntity(order);

        assertEquals("order-1", entity.getId());
        assertEquals("customer-1", entity.getCustomerId());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(entity.getTotalAmount()));
        assertEquals(OrderStatus.CREATED.name(), entity.getStatus());
        assertEquals(2, entity.getItems().size());
    }

    @Test
    void mapsJpaEntityToDomainOrder() {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId("order-1");
        entity.setCustomerId("customer-1");
        entity.setTotalAmount(BigDecimal.TEN);
        entity.setStatus(OrderStatus.CREATED.name());

        OrderItemJpaEmbeddable item = new OrderItemJpaEmbeddable();
        item.setProductId("product-1");
        item.setQuantity(1);
        item.setPrice(BigDecimal.TEN);
        entity.getItems().add(item);

        Order order = mapper.toDomain(entity);

        assertEquals("order-1", order.getId());
        assertEquals("customer-1", order.getCustomerId());
        assertEquals(0, BigDecimal.TEN.compareTo(order.getTotalAmount()));
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(1, order.getItems().size());
    }
}
