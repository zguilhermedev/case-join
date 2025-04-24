package com.join.order_service.domain.service;

import com.join.order_service.application.dto.request.OrderCreationDTO;
import com.join.order_service.application.dto.response.OrderDTO;
import com.join.order_service.application.mapper.OrderMapper;
import com.join.order_service.domain.entity.OrderEntity;
import com.join.order_service.domain.entity.OrderProductEntity;
import com.join.order_service.domain.entity.OrderProductId;
import com.join.order_service.domain.entity.enums.Status;
import com.join.order_service.domain.exception.NotFoundException;
import com.join.order_service.infrastructure.repository.OrderProductRepository;
import com.join.order_service.infrastructure.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ReactiveKafkaProducerTemplate<Object, Object> jsonReactiveProducer;
    private final OrderRepository repository;
    private final OrderProductRepository orderProductRepository;
    private final OrderMapper mapper;

    public void createOrder(OrderCreationDTO dto) {
        OrderEntity order = new OrderEntity();
        order.setUserId(dto.getUserId());
        order.setAmount(dto.getAmount());
        order.setStatus(Status.CREATED);

        OrderEntity savedOrder = repository.save(order);

        List<OrderProductEntity> orderProducts = dto.getProducts().entrySet().stream()
                .map(entry -> {
                    OrderProductEntity op = new OrderProductEntity();
                    op.setId(new OrderProductId(savedOrder.getId(), entry.getKey()));
                    op.setProductQuantity(entry.getValue());
                    return op;
                }).toList();

        orderProductRepository.saveAll(orderProducts);

        jsonReactiveProducer.send("order-created", dto)
                .publishOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Transactional
    public OrderDTO updateOrder(Long orderId, OrderCreationDTO dto) {
        OrderEntity order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id " + orderId));

        order.setUserId(dto.getUserId());
        order.setAmount(dto.getAmount());
        repository.save(order);

        List<OrderProductEntity> existingProducts = orderProductRepository.findAllByIdOrderId(orderId);
        Set<Long> existingProductIds = existingProducts.stream()
                .map(op -> op.getId().getProductId())
                .collect(Collectors.toSet());

        Set<Long> incomingProductIds = dto.getProducts().keySet();

        // Delete removed products
        List<OrderProductEntity> toRemove = existingProducts.stream()
                .filter(op -> !incomingProductIds.contains(op.getId().getProductId()))
                .toList();
        orderProductRepository.deleteAll(toRemove);

        // Create or update remaining/new products
        List<OrderProductEntity> updatedProducts = dto.getProducts().entrySet().stream()
                .map(entry -> {
                    OrderProductEntity op = new OrderProductEntity();
                    op.setId(new OrderProductId(orderId, entry.getKey()));
                    op.setProductQuantity(entry.getValue());
                    return op;
                })
                .toList();
        orderProductRepository.saveAll(updatedProducts);

        // Fetch again to ensure fresh state
        List<OrderProductEntity> finalProducts = orderProductRepository.findAllByIdOrderId(orderId);
        return mapper.toDto(order, finalProducts);
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        orderProductRepository.deleteByOrderId(id);

        repository.deleteById(id);
    }

    public OrderDTO findById(Long id) {
        OrderEntity order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderProductEntity> orderProducts = orderProductRepository.findAllByIdOrderId(id);

        return mapper.toDto(order, orderProducts);
    }

    public Page<OrderDTO> findAll(Optional<Long> userId, Pageable pageable) {
        Page<OrderEntity> page = userId
                .map(id -> repository.findByUserId(id, pageable))
                .orElseGet(() -> repository.findAll(pageable));

        List<Long> orderIds = page.getContent().stream()
                .map(OrderEntity::getId)
                .toList();

        List<OrderProductEntity> orderProducts = orderProductRepository.findAllByIdOrderIdIn(orderIds);

        Map<Long, List<OrderProductEntity>> orderProductMap = orderProducts.stream()
                .collect(Collectors.groupingBy(op -> op.getId().getOrderId()));

        List<OrderDTO> dtos = page.getContent().stream()
                .map(order -> mapper.toDto(order, orderProductMap.getOrDefault(order.getId(), List.of())))
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

}
