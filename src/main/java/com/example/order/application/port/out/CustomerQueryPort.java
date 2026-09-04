package com.example.order.application.port.out;

public interface CustomerQueryPort {

    boolean existsById(String customerId);
}
