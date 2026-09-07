package com.example.order.config;

import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.port.in.PublishOutboxEventsUseCase;
import com.example.order.application.port.out.CustomerQueryPort;
import com.example.order.application.port.out.InventoryPort;
import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.port.out.OutboxRepositoryPort;
import com.example.order.application.service.CreateOrderService;
import com.example.order.application.service.GetOrderService;
import com.example.order.application.service.PublishOutboxEventsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class BeanConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            CustomerQueryPort customerQueryPort,
            InventoryPort inventoryPort,
            OrderRepositoryPort orderRepository,
            OutboxRepositoryPort outboxRepository
    ) {
        CreateOrderService service = new CreateOrderService(
                customerQueryPort,
                inventoryPort,
                orderRepository,
                outboxRepository
        );
        return new TransactionalCreateOrderUseCase(service);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepositoryPort orderRepository) {
        return new GetOrderService(orderRepository);
    }

    @Bean
    public PublishOutboxEventsUseCase publishOutboxEventsUseCase(
            OutboxRepositoryPort outboxRepository,
            OrderEventPublisherPort orderEventPublisher
    ) {
        return new PublishOutboxEventsService(outboxRepository, orderEventPublisher);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
