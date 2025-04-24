package com.join.bff.domain.dto.response;

import com.join.bff.application.dto.response.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private CategoryDTO category;
    private BigDecimal value;
}
