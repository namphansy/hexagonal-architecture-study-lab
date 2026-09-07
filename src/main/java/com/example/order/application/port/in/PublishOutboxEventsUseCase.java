package com.example.order.application.port.in;

public interface PublishOutboxEventsUseCase {

    void publishPendingEvents();
}
