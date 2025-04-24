package com.join.order_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.join.order_service.application.dto.request.OrderCreationDTO;
import com.join.order_service.domain.entity.OrderEntity;
import com.join.order_service.domain.entity.OrderProductEntity;
import com.join.order_service.domain.entity.OrderProductId;
import com.join.order_service.domain.entity.enums.Status;
import com.join.order_service.infrastructure.repository.OrderProductRepository;
import com.join.order_service.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver"); // Explicit driver
    }

    @BeforeEach
    void setup() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_ShouldReturnSuccess() throws Exception {

        OrderCreationDTO dto = new OrderCreationDTO();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setProducts(Map.of(1l, 2));

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Order saved successfully"));

        assertEquals(1, orderRepository.count());
        assertEquals(1, orderProductRepository.count());
    }

    @Test
    void updateOrder_ShouldUpdateSuccessfully() throws Exception {

        OrderEntity order = new OrderEntity();
        order.setUserId(1L);
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus(Status.CREATED);
        order = orderRepository.save(order);

        OrderProductEntity orderProduct = new OrderProductEntity();
        orderProduct.setId(new OrderProductId(order.getId(), 1L));
        orderProduct.setProductQuantity(1);
        orderProductRepository.save(orderProduct);

        OrderCreationDTO dto = new OrderCreationDTO();
        dto.setUserId(2L); // Changed user
        dto.setAmount(new BigDecimal("200.00")); // Changed amount
        dto.setProducts(Map.of(1L, 3)); // Changed quantity

        mockMvc.perform(put("/order/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Order updated successfully"));

        OrderEntity updatedOrder = orderRepository.findById(order.getId()).get();
        assertEquals(2L, updatedOrder.getUserId());
        assertEquals(new BigDecimal("200.00"), updatedOrder.getAmount());

        OrderProductEntity updatedProduct = orderProductRepository.findAllByIdOrderId(order.getId()).get(0);
        assertEquals(3, updatedProduct.getProductQuantity());
    }

    @Test
    void updateOrder_ShouldReturnNotFoundWhenOrderNotExist() throws Exception {
        OrderCreationDTO dto = new OrderCreationDTO();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setProducts(Map.of(1L, 1));

        mockMvc.perform(put("/order/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ERROR"));
    }

}
