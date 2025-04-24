package com.join.order_service.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderCreationDTO {
    private Long userId;
    private BigDecimal amount;
    private Map<Long, Integer> products = new HashMap<Long, Integer>();
}
