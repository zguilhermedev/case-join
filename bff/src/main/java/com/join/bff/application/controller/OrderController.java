package com.join.bff.application.controller;

import com.join.bff.application.dto.request.OrderCreationDTO;
import com.join.bff.application.dto.response.OrderDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import com.join.bff.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody OrderCreationDTO dto) {
        return service.saveOrder(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id,
                                              @RequestBody OrderCreationDTO dto) {
        return service.updateOrder(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return service.deleteOrder(id);
    }

    @GetMapping
    public Page<OrderDTO> findAll(@RequestParam(required = false) Optional<Long> userId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return service.findAllOrdersWithProducts(userId, page, size);
    }

}
