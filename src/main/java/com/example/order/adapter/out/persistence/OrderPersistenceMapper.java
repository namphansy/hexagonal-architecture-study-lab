package com.example.order.adapter.out.persistence;

import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.model.OrderStatus;

import java.util.ArrayList;
import java.util.List;

class OrderPersistenceMapper {

    OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus().name());
        entity.setItems(toItemEntities(order.getItems()));
        return entity;
    }

    Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                toDomainItems(entity.getItems()),
                entity.getTotalAmount(),
                OrderStatus.valueOf(entity.getStatus())
        );
    }

    private List<OrderItemJpaEmbeddable> toItemEntities(List<OrderItem> items) {
        List<OrderItemJpaEmbeddable> itemEntities = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemJpaEmbeddable itemEntity = new OrderItemJpaEmbeddable();
            itemEntity.setProductId(item.getProductId());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setPrice(item.getPrice());
            itemEntities.add(itemEntity);
        }
        return itemEntities;
    }

    private List<OrderItem> toDomainItems(List<OrderItemJpaEmbeddable> itemEntities) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemJpaEmbeddable itemEntity : itemEntities) {
            items.add(new OrderItem(itemEntity.getProductId(), itemEntity.getQuantity(), itemEntity.getPrice()));
        }
        return items;
    }
}
