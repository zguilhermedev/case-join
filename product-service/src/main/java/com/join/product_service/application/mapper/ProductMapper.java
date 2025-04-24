package com.join.product_service.application.mapper;

import com.join.product_service.application.dto.request.ProductCreationDTO;
import com.join.product_service.application.dto.response.CategoryDTO;
import com.join.product_service.application.dto.response.ProductDTO;
import com.join.product_service.domain.entity.CategoryEntity;
import com.join.product_service.domain.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category", target = "category")
    ProductDTO toDto(ProductEntity entity);

    ProductEntity toEntity(ProductDTO dto);

    ProductEntity creationToEntity(ProductCreationDTO dto);

    List<ProductDTO> toDTOList(List<ProductEntity> products);

    CategoryDTO toCategoryDTO(CategoryEntity category);

}
