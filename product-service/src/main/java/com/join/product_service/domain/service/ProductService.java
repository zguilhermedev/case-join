package com.join.product_service.domain.service;

import com.join.product_service.application.dto.request.ProductCreationDTO;
import com.join.product_service.application.dto.response.ProductDTO;
import com.join.product_service.application.mapper.ProductMapper;
import com.join.product_service.domain.entity.CategoryEntity;
import com.join.product_service.domain.entity.ProductEntity;
import com.join.product_service.domain.exception.NotFoundException;
import com.join.product_service.infrastructure.repository.CategoryRepository;
import com.join.product_service.infrastructure.repository.OrderProductRepository;
import com.join.product_service.infrastructure.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductMapper mapper;

    public void save(ProductCreationDTO dto) {

        Optional<CategoryEntity> opt = categoryRepository.findById(dto.getCategoryId());
        if (opt.isEmpty()) {
            throw new NotFoundException("Category with id %s not found".formatted(dto.getCategoryId()));
        }

        ProductEntity pe = mapper.creationToEntity(dto);
        pe.setCategory(opt.get());
        repository.save(pe);
    }

    public void update(Long id, ProductCreationDTO dto) {
        Optional<ProductEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Product not found");
        }


        Optional<CategoryEntity> optCategory = categoryRepository.findById(dto.getCategoryId());
        if (optCategory.isEmpty()) {
            throw new NotFoundException("Category with id %s not found".formatted(dto.getCategoryId()));
        }

        ProductEntity pe = mapper.creationToEntity(dto);
        pe.setCategory(optCategory.get());

        ProductEntity entity = mapper.creationToEntity(dto);
        entity.setId(opt.get().getId());
        entity.setCategory(optCategory.get());
        repository.save(entity);
    }


    @Transactional
    public void delete(Long id) {
        Optional<ProductEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        orderProductRepository.deleteByProductId(id);

        repository.delete(opt.get());
    }

    public ProductDTO findById(Long id) {
        Optional<ProductEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        ProductEntity product = opt.get();
        return mapper.toDto(product);
    }

    public Page<ProductDTO> findAll(Long categoryId, Pageable pageable) {
        Page<ProductEntity> page;

        if (categoryId != null) {
            page = repository.findByCategoryId(categoryId, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDto);
    }

    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        List<ProductEntity> products = repository.findAllWithCategoryByIdIn(ids);
        return mapper.toDTOList(products);
    }

}
