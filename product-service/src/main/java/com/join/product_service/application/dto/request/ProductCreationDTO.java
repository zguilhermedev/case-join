package com.join.product_service.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductCreationDTO {
    private String name;
    private Long categoryId;
    private BigDecimal value;
}
