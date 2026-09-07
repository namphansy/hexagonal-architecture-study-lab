package com.example.order.adapter.out.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class InventoryRestAdapterTest {

    @Test
    void isAvailableReturnsTrueWhenInventoryServiceReturnsAvailable() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        InventoryRestAdapter adapter = new InventoryRestAdapter(restTemplate, "http://inventory-service");

        server.expect(once(), requestTo("http://inventory-service/inventory/product-1/availability?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"available\":true}", MediaType.APPLICATION_JSON));

        assertTrue(adapter.isAvailable("product-1", 2));
        server.verify();
    }

    @Test
    void isAvailableReturnsFalseWhenInventoryServiceReturnsUnavailable() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        InventoryRestAdapter adapter = new InventoryRestAdapter(restTemplate, "http://inventory-service");

        server.expect(once(), requestTo("http://inventory-service/inventory/product-1/availability?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"available\":false}", MediaType.APPLICATION_JSON));

        assertFalse(adapter.isAvailable("product-1", 2));
        server.verify();
    }

    @Test
    void isAvailableReturnsFalseWhenInventoryServiceReturnsNotFound() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        InventoryRestAdapter adapter = new InventoryRestAdapter(restTemplate, "http://inventory-service");

        server.expect(once(), requestTo("http://inventory-service/inventory/missing-product/availability?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertFalse(adapter.isAvailable("missing-product", 2));
        server.verify();
    }

    @Test
    void isAvailableTranslatesTechnicalFailure() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        InventoryRestAdapter adapter = new InventoryRestAdapter(restTemplate, "http://inventory-service");

        server.expect(once(), requestTo("http://inventory-service/inventory/product-1/availability?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(InventoryServiceException.class, () -> adapter.isAvailable("product-1", 2));
        server.verify();
    }
}
