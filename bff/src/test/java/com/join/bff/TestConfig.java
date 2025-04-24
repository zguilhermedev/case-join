package com.join.bff;

import com.join.bff.domain.service.OrderService;
import com.join.bff.domain.service.ProductService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ProductService productService(RestTemplate restTemplate) {
        return new ProductService(restTemplate, "http://localhost:8081");
    }

    @Bean
    @Primary
    public OrderService orderService(ProductService productService, RestTemplate restTemplate) {
        return new OrderService(productService, restTemplate, "http://localhost:8082");
    }
}
