package com.example.order.adapter.out.persistence;

import com.example.order.application.outbox.OutboxEvent;
import com.example.order.application.port.out.OutboxRepositoryPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("in-memory")
public class InMemoryOutboxRepositoryAdapter implements OutboxRepositoryPort {

    private final Map<String, OutboxEvent> events = new ConcurrentHashMap<>();

    @Override
    public OutboxEvent save(OutboxEvent event) {
        events.put(event.getId(), event);
        return event;
    }

    @Override
    public List<OutboxEvent> findUnpublished(int limit) {
        return new ArrayList<>(events.values())
                .stream()
                .filter(event -> event.getPublishedAt() == null)
                .sorted(Comparator.comparing(OutboxEvent::getOccurredAt))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsPublished(String eventId) {
        OutboxEvent event = events.get(eventId);
        if (event != null) {
            events.put(eventId, event.markPublished(Instant.now()));
        }
    }
}
