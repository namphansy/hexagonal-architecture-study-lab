package com.example.order.application.port.out;

import com.example.order.application.event.OrderCreatedEvent;

public interface OrderEventPublisherPort {

    void publishOrderCreated(OrderCreatedEvent event);
}
