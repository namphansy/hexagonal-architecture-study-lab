package com.example.order.config;

import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.service.CreateOrderService;
import com.example.order.application.service.GetOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepositoryPort orderRepository) {
        return new CreateOrderService(orderRepository);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepositoryPort orderRepository) {
        return new GetOrderService(orderRepository);
    }
}
