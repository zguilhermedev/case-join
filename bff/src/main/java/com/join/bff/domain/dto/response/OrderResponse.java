package com.join.bff.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private Map<Long, Integer> products = new HashMap<>();
}
