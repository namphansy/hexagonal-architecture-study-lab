package com.example.order.adapter.in.web;

import com.example.order.application.port.in.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderResult;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.domain.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(
        CreateOrderUseCase createOrderUseCase, 
        GetOrderUseCase getOrderUseCase
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(request.getCustomerId(), toCommandItems(request.getItems()));
        CreateOrderResult result = createOrderUseCase.createOrder(command);
        return ResponseEntity.created(URI.create("/orders/" + result.getOrderId()))
                .body(new CreateOrderResponse(result.getOrderId(), result.getTotalAmount(), result.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        return getOrderUseCase.getOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private List<CreateOrderCommand.Item> toCommandItems(List<CreateOrderRequest.ItemRequest> items) {
        List<CreateOrderCommand.Item> commandItems = new ArrayList<>();
        if (items == null) {
            return commandItems;
        }
        for (CreateOrderRequest.ItemRequest item : items) {
            commandItems.add(new CreateOrderCommand.Item(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return commandItems;
    }
}
