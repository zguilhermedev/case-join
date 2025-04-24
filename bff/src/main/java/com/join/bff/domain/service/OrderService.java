package com.join.bff.domain.service;

import com.join.bff.application.dto.request.OrderCreationDTO;
import com.join.bff.application.dto.response.OrderDTO;
import com.join.bff.application.dto.response.ProductWithQuantityDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import com.join.bff.domain.dto.response.OrderResponse;
import com.join.bff.domain.dto.response.PageResponse;
import com.join.bff.domain.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ProductService productService;

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public OrderService(ProductService productService, RestTemplate restTemplate, @Value("${services.order.base-url}") String baseUrl) {
        this.productService = productService;
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<OrderDTO> findById(Long id) {

        ResponseEntity<OrderResponse> response = restTemplate.exchange(baseUrl + "/order/" + id, HttpMethod.GET, HttpEntity.EMPTY, OrderResponse.class);
        OrderResponse orderResponse = response.getBody();
        if (orderResponse == null) {
            throw new RuntimeException("Order Not found");
        }

        Set<Long> allProductIds = new HashSet<>(orderResponse.getProducts().keySet());

        List<ProductResponse> products = productService.getAllByIds(new ArrayList<>(allProductIds));

        Map<Long, ProductResponse> productMap = products.stream()
                .collect(Collectors.toMap(ProductResponse::getId, Function.identity()));

        List<ProductWithQuantityDTO> productWithQuantityList = orderResponse.getProducts().entrySet().stream()
                .map(entry -> new ProductWithQuantityDTO(productMap.get(entry.getKey()), entry.getValue()))
                .toList();

        OrderDTO orderDTO = new OrderDTO(orderResponse.getId(), orderResponse.getUserId(), orderResponse.getAmount(), productWithQuantityList);

        return ResponseEntity.ok(orderDTO);
    }

    public ResponseEntity<ResponseDTO> saveOrder(OrderCreationDTO dto) {
        return restTemplate.postForEntity(baseUrl + "/order", new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> updateOrder(Long id, OrderCreationDTO dto) {
        return restTemplate.exchange(baseUrl + "/order/" + id, HttpMethod.PUT, new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> deleteOrder(Long id) {
        return restTemplate.exchange(baseUrl + "/order/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, ResponseDTO.class);
    }

    public Page<OrderDTO> findAllOrdersWithProducts(Optional<Long> userId, int page, int size) {
        StringBuilder url = new StringBuilder(baseUrl + "/order?page=" + page + "&size=" + size);
        userId.ifPresent(uid -> url.append("&userId=").append(uid));

        ParameterizedTypeReference<PageResponse<OrderResponse>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<PageResponse<OrderResponse>> response = restTemplate.exchange(
                url.toString(), HttpMethod.GET, HttpEntity.EMPTY, responseType);

        PageResponse<OrderResponse> orderPage = response.getBody();
        if (orderPage == null || orderPage.isEmpty()) {
            return Page.empty();
        }

        List<OrderResponse> orders = orderPage.getContent();

        Set<Long> allProductIds = orders.stream()
                .flatMap(order -> order.getProducts().keySet().stream())
                .collect(Collectors.toSet());

        List<ProductResponse> products = productService.getAllByIds(new ArrayList<>(allProductIds));

        Map<Long, ProductResponse> productMap = products.stream()
                .collect(Collectors.toMap(ProductResponse::getId, Function.identity()));

        List<OrderDTO> enrichedOrders = orders.stream()
                .map(order -> {
                    List<ProductWithQuantityDTO> productWithQuantityList = order.getProducts().entrySet().stream()
                            .map(entry -> new ProductWithQuantityDTO(productMap.get(entry.getKey()), entry.getValue()))
                            .collect(Collectors.toList());

                    return new OrderDTO(order.getId(), order.getUserId(), order.getAmount(), productWithQuantityList);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(enrichedOrders,
                PageRequest.of(orderPage.getNumber(), orderPage.getSize()),
                orderPage.getTotalElements());
    }


}
