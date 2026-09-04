package com.example.order.adapter.out.customer;

import com.example.order.application.port.out.CustomerQueryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerRestAdapter implements CustomerQueryPort {

    private final RestTemplate restTemplate;
    private final String customerServiceBaseUrl;

    public CustomerRestAdapter(
            RestTemplate restTemplate,
            @Value("${external.customer-service.base-url}") String customerServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.customerServiceBaseUrl = customerServiceBaseUrl;
    }

    @Override
    public boolean existsById(String customerId) {
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(
                    customerServiceBaseUrl + "/customers/{customerId}",
                    Void.class,
                    customerId
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        } catch (RestClientException exception) {
            throw new CustomerServiceException("Failed to query customer service.", exception);
        }
    }
}
