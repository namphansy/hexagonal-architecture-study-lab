package com.example.order.config;

import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.service.CreateOrderService;
import com.example.order.application.service.GetOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase() {
        return new CreateOrderService();
    }

    @Bean
    public GetOrderUseCase getOrderUseCase() {
        return new GetOrderService();
    }
}
