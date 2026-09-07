package com.example.order.application.port.out;

import com.example.order.application.outbox.OutboxEvent;

import java.util.List;

public interface OutboxRepositoryPort {

    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findUnpublished(int limit);

    void markAsPublished(String eventId);
}
