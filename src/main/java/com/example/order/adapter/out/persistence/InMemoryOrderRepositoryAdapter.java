package com.example.order.adapter.out.persistence;

import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("in-memory")
public class InMemoryOrderRepositoryAdapter implements OrderRepositoryPort {

    @Override
    public Order save(Order order) {
        throw new UnsupportedOperationException("In-memory adapter exercise intentionally left blank.");
    }

    @Override
    public Optional<Order> findById(String id) {
        throw new UnsupportedOperationException("In-memory adapter exercise intentionally left blank.");
    }
}
