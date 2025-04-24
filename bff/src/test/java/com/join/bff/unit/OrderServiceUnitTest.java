package com.join.bff.unit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.join.bff.application.dto.response.OrderDTO;
import com.join.bff.domain.dto.response.OrderResponse;
import com.join.bff.domain.dto.response.PageResponse;
import com.join.bff.domain.dto.response.ProductResponse;
import com.join.bff.domain.service.OrderService;
import com.join.bff.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private ProductService productService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void findAllOrdersWithProducts_ShouldReturnEnrichedOrders() {
        // Mock response from Order microservice
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setAmount(new BigDecimal("100.00"));
        orderResponse.setProducts(Map.of(1L, 2));

        PageResponse<OrderResponse> mockPage = new PageResponse<>();
        mockPage.setContent(List.of(orderResponse));
        mockPage.setTotalElements(1);
        mockPage.setNumber(0);
        mockPage.setSize(10);

        ResponseEntity<PageResponse<OrderResponse>> mockResponse =
                ResponseEntity.ok(mockPage);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(HttpEntity.EMPTY),
                any(ParameterizedTypeReference.class))
        ).thenReturn(mockResponse);

        // Mock product service response
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Product 1");
        productResponse.setValue(new BigDecimal("50.00"));

        when(productService.getAllByIds(anyList()))
                .thenReturn(List.of(productResponse));


        Page<OrderDTO> result = orderService.findAllOrdersWithProducts(Optional.empty(), 0, 10);

        assertEquals(1, result.getTotalElements());
        OrderDTO orderDTO = result.getContent().get(0);
        assertEquals(1L, orderDTO.getId());
        assertEquals(1, orderDTO.getProducts().size());
        assertEquals("Product 1", orderDTO.getProducts().get(0).getProduct().getName());
        assertEquals(2, orderDTO.getProducts().get(0).getQuantity());
    }

    @Test
    void findById_ShouldReturnEnrichedOrder() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setAmount(new BigDecimal("100.00"));
        orderResponse.setProducts(Map.of(1L, 2));

        ResponseEntity<OrderResponse> mockResponse = ResponseEntity.ok(orderResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(HttpEntity.EMPTY),
                eq(OrderResponse.class))
        ).thenReturn(mockResponse);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Product 1");
        productResponse.setValue(new BigDecimal("50.00"));

        when(productService.getAllByIds(anyList()))
                .thenReturn(List.of(productResponse));

        ResponseEntity<OrderDTO> result = orderService.findById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        OrderDTO orderDTO = result.getBody();
        assertNotNull(orderDTO);
        assertEquals(1L, orderDTO.getId());
        assertEquals(1, orderDTO.getProducts().size());
        assertEquals("Product 1", orderDTO.getProducts().get(0).getProduct().getName());
        assertEquals(2, orderDTO.getProducts().get(0).getQuantity());
    }

    @Test
    void findById_ShouldThrowExceptionWhenOrderNotFound() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(HttpEntity.EMPTY),
                eq(OrderResponse.class))
        ).thenReturn(ResponseEntity.notFound().build());

        assertThrows(RuntimeException.class, () -> {
            orderService.findById(1L);
        });
    }
}
