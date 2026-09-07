package com.example.order.adapter.in.messaging;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateOrderKafkaListener {

    private final CreateOrderUseCase createOrderUseCase;

    public CreateOrderKafkaListener(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @KafkaListener(topics = "${messaging.kafka.topics.create-order-command}")
    public void handle(CreateOrderMessage message) {
        createOrderUseCase.createOrder(toCommand(message));
    }

    private CreateOrderCommand toCommand(CreateOrderMessage message) {
        return new CreateOrderCommand(message.getCustomerId(), toCommandItems(message.getItems()));
    }

    private List<CreateOrderCommand.Item> toCommandItems(List<CreateOrderMessage.ItemMessage> items) {
        List<CreateOrderCommand.Item> commandItems = new ArrayList<>();
        if (items == null) {
            return commandItems;
        }
        for (CreateOrderMessage.ItemMessage item : items) {
            commandItems.add(new CreateOrderCommand.Item(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return commandItems;
    }
}
