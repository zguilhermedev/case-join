package com.join.order_service.application.mapper;

import com.join.order_service.application.dto.request.OrderCreationDTO;
import com.join.order_service.application.dto.response.OrderDTO;
import com.join.order_service.domain.entity.OrderEntity;
import com.join.order_service.domain.entity.OrderProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "products", ignore = true)
    OrderDTO toDto(OrderEntity entity);

    default OrderDTO toDto(OrderEntity entity, List<OrderProductEntity> orderProducts) {
        OrderDTO dto = toDto(entity);

        Map<Long, Integer> productMap = orderProducts.stream()
                .collect(Collectors.toMap(
                        op -> op.getId().getProductId(),
                        OrderProductEntity::getProductQuantity
                ));

        dto.setProducts(productMap);
        return dto;
    }
}
