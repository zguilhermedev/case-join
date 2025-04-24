package com.join.category_service.infrastructure.repository;

import com.join.category_service.domain.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
