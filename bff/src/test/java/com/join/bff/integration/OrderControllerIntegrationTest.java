package com.join.bff.integration;

import com.join.bff.SecurityTestConfig;
import com.join.bff.TestConfig;
import com.join.bff.application.controller.OrderController;
import com.join.bff.application.dto.response.CategoryDTO;
import com.join.bff.application.dto.response.OrderDTO;
import com.join.bff.application.dto.response.ProductWithQuantityDTO;
import com.join.bff.domain.dto.response.ProductResponse;
import com.join.bff.domain.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import({TestConfig.class, SecurityTestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void findById_ShouldReturnOrder() throws Exception {
        // Arrange
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Electronics");
        ProductWithQuantityDTO productWithQuantity = new ProductWithQuantityDTO(
                new ProductResponse(1L, "Product 1", categoryDTO, new BigDecimal("50.00")),
                2
        );

        OrderDTO orderDTO = new OrderDTO(
                1L, 1L, new BigDecimal("100.00"),
                List.of(productWithQuantity)
        );

        when(orderService.findById(1L))
                .thenReturn(ResponseEntity.ok(orderDTO));

        mockMvc.perform(get("/order/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.products[0].product.id").value(1))
                .andExpect(jsonPath("$.products[0].product.name").value("Product 1"))
                .andExpect(jsonPath("$.products[0].product.category.id").value(1))
                .andExpect(jsonPath("$.products[0].product.category.name").value("Electronics"))
                .andExpect(jsonPath("$.products[0].quantity").value(2));
    }
}
