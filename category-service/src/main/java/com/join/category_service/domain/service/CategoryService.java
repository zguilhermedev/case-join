package com.join.category_service.domain.service;

import com.join.category_service.application.dto.request.CategoryCreationDTO;
import com.join.category_service.application.dto.response.CategoryDTO;
import com.join.category_service.application.mapper.CategoryMapper;
import com.join.category_service.domain.entity.CategoryEntity;
import com.join.category_service.domain.exception.NotFoundException;
import com.join.category_service.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public void save(CategoryCreationDTO categoryDTO) {
        repository.save(mapper.creationToEntity(categoryDTO));
    }

    public void update(CategoryDTO categoryDTO) {
        Optional<CategoryEntity> opt = repository.findById(categoryDTO.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        repository.save(mapper.toEntity(categoryDTO));
    }

    public void delete(Long id) {
        Optional<CategoryEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        repository.delete(opt.get());
    }

    public CategoryDTO findById(Long id) {
        Optional<CategoryEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Category not found");
        }

        CategoryEntity categoryEntity = opt.get();
        return mapper.toDto(categoryEntity);
    }

    public Page<CategoryDTO> findAll(String name, Pageable pageable) {
        Page<CategoryEntity> page;

        if (name != null && !name.isEmpty()) {
            page = repository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDto);
    }

}
