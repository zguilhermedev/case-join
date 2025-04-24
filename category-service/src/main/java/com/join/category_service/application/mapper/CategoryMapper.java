package com.join.category_service.application.mapper;

import com.join.category_service.application.dto.request.CategoryCreationDTO;
import com.join.category_service.application.dto.response.CategoryDTO;
import com.join.category_service.domain.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDto(CategoryEntity entity);

    CategoryEntity toEntity(CategoryDTO dto);

    CategoryEntity creationToEntity(CategoryCreationDTO dto);

}
