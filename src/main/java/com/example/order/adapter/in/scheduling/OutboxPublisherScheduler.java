package com.example.order.adapter.in.scheduling;

import com.example.order.application.port.in.PublishOutboxEventsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisherScheduler {

    private final PublishOutboxEventsUseCase publishOutboxEventsUseCase;

    public OutboxPublisherScheduler(PublishOutboxEventsUseCase publishOutboxEventsUseCase) {
        this.publishOutboxEventsUseCase = publishOutboxEventsUseCase;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms}")
    public void publishPendingEvents() {
        publishOutboxEventsUseCase.publishPendingEvents();
    }
}
