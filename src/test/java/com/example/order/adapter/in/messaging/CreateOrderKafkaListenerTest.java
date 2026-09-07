package com.example.order.adapter.in.messaging;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateOrderKafkaListenerTest {

    @Test
    void handleMapsKafkaMessageToCreateOrderUseCase() {
        CapturingCreateOrderUseCase useCase = new CapturingCreateOrderUseCase();
        CreateOrderKafkaListener listener = new CreateOrderKafkaListener(useCase);
        CreateOrderMessage message = new CreateOrderMessage();
        message.setCustomerId("customer-1");

        CreateOrderMessage.ItemMessage item = new CreateOrderMessage.ItemMessage();
        item.setProductId("product-1");
        item.setQuantity(2);
        item.setPrice(BigDecimal.TEN);
        message.getItems().add(item);

        listener.handle(message);

        assertTrue(useCase.lastCommand().isPresent());
        CreateOrderCommand command = useCase.lastCommand().get();
        assertEquals("customer-1", command.getCustomerId());
        assertEquals(1, command.getItems().size());
        assertEquals("product-1", command.getItems().get(0).getProductId());
    }

    private static class CapturingCreateOrderUseCase implements CreateOrderUseCase {

        private CreateOrderCommand lastCommand;

        @Override
        public CreateOrderResult createOrder(CreateOrderCommand command) {
            this.lastCommand = command;
            return new CreateOrderResult("order-1", BigDecimal.TEN, OrderStatus.CREATED);
        }

        Optional<CreateOrderCommand> lastCommand() {
            return Optional.ofNullable(lastCommand);
        }
    }
}
