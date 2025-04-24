package com.join.bff.application.dto.response;

import com.join.bff.domain.dto.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductWithQuantityDTO {
    private ProductResponse product;
    private Integer quantity;
}
