package com.join.order_service.unit;

import com.join.order_service.application.dto.request.OrderCreationDTO;
import com.join.order_service.application.dto.response.OrderDTO;
import com.join.order_service.application.mapper.OrderMapper;
import com.join.order_service.domain.entity.OrderEntity;
import com.join.order_service.domain.entity.OrderProductEntity;
import com.join.order_service.domain.entity.OrderProductId;
import com.join.order_service.domain.exception.NotFoundException;
import com.join.order_service.domain.service.OrderService;
import com.join.order_service.infrastructure.repository.OrderProductRepository;
import com.join.order_service.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {


    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;


    @Test
    void updateOrder_ShouldUpdateExistingOrder() {
        // Arrange
        Long orderId = 1L;
        OrderCreationDTO dto = new OrderCreationDTO();
        dto.setUserId(2L);
        dto.setAmount(new BigDecimal("200.00"));
        dto.setProducts(Map.of(1L, 5));

        OrderEntity existingOrder = new OrderEntity();
        existingOrder.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        OrderProductEntity existingProduct = new OrderProductEntity();
        existingProduct.setId(new OrderProductId(orderId, 2L));
        when(orderProductRepository.findAllByIdOrderId(orderId)).thenReturn(List.of(existingProduct));

        OrderDTO expectedDto = new OrderDTO();
        when(orderMapper.toDto(any(), any())).thenReturn(expectedDto);

        // Act
        OrderDTO result = orderService.updateOrder(orderId, dto);

        // Assert
        assertEquals(expectedDto, result);
        verify(orderRepository).save(argThat(order ->
                order.getUserId().equals(2L) &&
                order.getAmount().equals(new BigDecimal("200.00"))
        ));
        verify(orderProductRepository).deleteAll(argThat(iterable -> {
            List<OrderProductEntity> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list.size() == 1 &&
                   list.get(0).getId().getProductId().equals(2L);
        }));

        verify(orderProductRepository).saveAll(argThat(iterable -> {
            List<OrderProductEntity> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list.size() == 1 &&
                   list.get(0).getId().getProductId().equals(1L) &&
                   list.get(0).getProductQuantity() == 5;
        }));
    }

    @Test
    void updateOrder_ShouldThrowExceptionWhenOrderNotFound() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                orderService.updateOrder(orderId, new OrderCreationDTO())
        );
    }



}
