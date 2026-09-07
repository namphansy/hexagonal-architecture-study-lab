package com.example.order.adapter.out.inventory;

import com.example.order.application.port.out.InventoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryRestAdapter implements InventoryPort {

    private final RestTemplate restTemplate;
    private final String inventoryServiceBaseUrl;

    public InventoryRestAdapter(
            RestTemplate restTemplate,
            @Value("${external.inventory-service.base-url}") String inventoryServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.inventoryServiceBaseUrl = inventoryServiceBaseUrl;
    }

    @Override
    public boolean isAvailable(String productId, int quantity) {
        try {
            ResponseEntity<InventoryAvailabilityResponse> response = restTemplate.getForEntity(
                    inventoryServiceBaseUrl + "/inventory/{productId}/availability?quantity={quantity}",
                    InventoryAvailabilityResponse.class,
                    productId,
                    quantity
            );
            InventoryAvailabilityResponse body = response.getBody();
            return response.getStatusCode().is2xxSuccessful() && body != null && body.isAvailable();
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        } catch (RestClientException exception) {
            throw new InventoryServiceException("Failed to query inventory service.", exception);
        }
    }
}
