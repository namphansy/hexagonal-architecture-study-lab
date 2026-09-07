package com.example.order.application.service;

import com.example.order.application.outbox.OutboxEvent;
import com.example.order.application.port.in.PublishOutboxEventsUseCase;
import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.application.port.out.OutboxRepositoryPort;

import java.util.List;

public class PublishOutboxEventsService implements PublishOutboxEventsUseCase {

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final OutboxRepositoryPort outboxRepository;
    private final OrderEventPublisherPort orderEventPublisher;

    public PublishOutboxEventsService(
            OutboxRepositoryPort outboxRepository,
            OrderEventPublisherPort orderEventPublisher
    ) {
        this.outboxRepository = outboxRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findUnpublished(DEFAULT_BATCH_SIZE);
        for (OutboxEvent event : events) {
            if (OutboxEvent.ORDER_CREATED_EVENT_TYPE.equals(event.getEventType())) {
                orderEventPublisher.publishOrderCreated(event.getPayload());
                outboxRepository.markAsPublished(event.getId());
            }
        }
    }
}
