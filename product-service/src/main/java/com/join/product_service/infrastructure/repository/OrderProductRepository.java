package com.join.product_service.infrastructure.repository;

import com.join.product_service.domain.entity.OrderProductEntity;
import com.join.product_service.domain.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderProductRepository extends JpaRepository<OrderProductEntity, OrderProductId> {

    @Modifying
    @Query("DELETE FROM OrderProductEntity op WHERE op.id.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);

}
