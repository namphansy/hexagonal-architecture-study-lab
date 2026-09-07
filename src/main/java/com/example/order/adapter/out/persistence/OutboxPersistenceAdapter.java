package com.example.order.adapter.out.persistence;

import com.example.order.application.outbox.OutboxEvent;
import com.example.order.application.port.out.OutboxRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("jpa")
public class OutboxPersistenceAdapter implements OutboxRepositoryPort {

    private final SpringDataOutboxRepository repository;
    private final OutboxPersistenceMapper mapper;

    public OutboxPersistenceAdapter(SpringDataOutboxRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = new OutboxPersistenceMapper(objectMapper);
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return mapper.toDomain(repository.save(mapper.toEntity(event)));
    }

    @Override
    public List<OutboxEvent> findUnpublished(int limit) {
        return repository.findByPublishedAtIsNullOrderByOccurredAtAsc(PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsPublished(String eventId) {
        repository.findById(eventId).ifPresent(entity -> {
            entity.setPublishedAt(Instant.now());
            repository.save(entity);
        });
    }
}
