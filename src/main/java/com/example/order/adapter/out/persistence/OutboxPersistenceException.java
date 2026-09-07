package com.example.order.adapter.out.persistence;

public class OutboxPersistenceException extends RuntimeException {

    public OutboxPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
