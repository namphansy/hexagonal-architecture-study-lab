package com.example.order.adapter.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOutboxRepository extends JpaRepository<OutboxJpaEntity, String> {

    List<OutboxJpaEntity> findByPublishedAtIsNullOrderByOccurredAtAsc(Pageable pageable);
}
