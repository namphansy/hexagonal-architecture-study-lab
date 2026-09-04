package com.example.order.adapter.out.customer;

import com.example.order.application.port.out.CustomerQueryPort;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestAdapter implements CustomerQueryPort {

    @Override
    public boolean existsById(String customerId) {
        throw new UnsupportedOperationException("Customer adapter exercise intentionally left blank.");
    }
}
