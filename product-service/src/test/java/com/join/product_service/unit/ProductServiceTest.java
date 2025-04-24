package com.join.product_service.unit;

import com.join.product_service.application.dto.request.ProductCreationDTO;
import com.join.product_service.application.mapper.ProductMapper;
import com.join.product_service.domain.entity.CategoryEntity;
import com.join.product_service.domain.entity.ProductEntity;
import com.join.product_service.domain.exception.NotFoundException;
import com.join.product_service.domain.service.ProductService;
import com.join.product_service.infrastructure.repository.CategoryRepository;
import com.join.product_service.infrastructure.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService service;

    private final BigDecimal value = new BigDecimal("100.00");

    @Test
    void shouldSaveProductWhenCategoryExists() {
        ProductCreationDTO dto = new ProductCreationDTO();
        dto.setName("Produto Teste");
        dto.setValue(value);
        dto.setCategoryId(10L);

        CategoryEntity category = new CategoryEntity();
        category.setId(10L);
        category.setName("Roupas");

        ProductEntity mapped = new ProductEntity();
        mapped.setName("Produto Teste");
        mapped.setValue(value);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(mapper.creationToEntity(dto)).thenReturn(mapped);

        service.save(dto);

        assertThat(mapped.getCategory()).isEqualTo(category);
        verify(repository).save(mapped);
    }

    @Test
    void shouldThrowNotFoundWhenCategoryNotExists() {
        ProductCreationDTO dto = new ProductCreationDTO();
        dto.setName("Produto Teste");
        dto.setValue(value);
        dto.setCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category with id 999 not found");

        verify(repository, never()).save(any());
        verify(mapper, never()).creationToEntity(any());
    }



}
