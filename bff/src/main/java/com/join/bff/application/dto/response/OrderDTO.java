package com.join.bff.application.dto.response;

import com.join.bff.domain.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private List<ProductWithQuantityDTO> products;

    public OrderDTO(OrderResponse order, List<ProductWithQuantityDTO> products) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.amount = order.getAmount();
        this.products = products;
    }
}
