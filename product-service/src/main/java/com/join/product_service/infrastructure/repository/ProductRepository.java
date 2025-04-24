package com.join.product_service.infrastructure.repository;

import com.join.product_service.domain.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.category WHERE p.id IN :ids")
    List<ProductEntity> findAllWithCategoryByIdIn(@Param("ids") List<Long> ids);

}
