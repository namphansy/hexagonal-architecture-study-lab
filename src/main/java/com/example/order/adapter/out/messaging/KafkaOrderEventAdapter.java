package com.example.order.adapter.out.messaging;

import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventAdapter implements OrderEventPublisherPort {

    @Override
    public void publishOrderCreated(Order order) {
        throw new UnsupportedOperationException("Messaging adapter exercise intentionally left blank.");
    }
}
