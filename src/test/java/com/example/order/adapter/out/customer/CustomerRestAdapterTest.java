package com.example.order.adapter.out.customer;

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

class CustomerRestAdapterTest {

    @Test
    void existsByIdReturnsTrueWhenCustomerServiceReturnsSuccess() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        CustomerRestAdapter adapter = new CustomerRestAdapter(restTemplate, "http://customer-service");

        server.expect(once(), requestTo("http://customer-service/customers/customer-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertTrue(adapter.existsById("customer-1"));
        server.verify();
    }

    @Test
    void existsByIdReturnsFalseWhenCustomerServiceReturnsNotFound() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        CustomerRestAdapter adapter = new CustomerRestAdapter(restTemplate, "http://customer-service");

        server.expect(once(), requestTo("http://customer-service/customers/missing-customer"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertFalse(adapter.existsById("missing-customer"));
        server.verify();
    }

    @Test
    void existsByIdTranslatesTechnicalFailure() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        CustomerRestAdapter adapter = new CustomerRestAdapter(restTemplate, "http://customer-service");

        server.expect(once(), requestTo("http://customer-service/customers/customer-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(CustomerServiceException.class, () -> adapter.existsById("customer-1"));
        server.verify();
    }
}
