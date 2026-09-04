package com.example.order.adapter.out.persistence;

import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;
    private final OrderPersistenceMapper mapper = new OrderPersistenceMapper();

    public OrderPersistenceAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity savedEntity = repository.save(mapper.toEntity(order));
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
