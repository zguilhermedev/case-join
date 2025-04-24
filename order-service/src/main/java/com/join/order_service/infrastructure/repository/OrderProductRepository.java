package com.join.order_service.infrastructure.repository;

import com.join.order_service.domain.entity.OrderProductEntity;
import com.join.order_service.domain.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, OrderProductId> {

    @Modifying
    @Query("DELETE FROM OrderProductEntity op WHERE op.id.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

    List<OrderProductEntity> findAllByIdOrderId(Long orderId);

    List<OrderProductEntity> findAllByIdOrderIdIn(List<Long> orderIds);

}
