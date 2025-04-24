package com.join.bff.application.controller;

import com.join.bff.application.dto.request.ProductCreationDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import com.join.bff.domain.dto.response.ProductResponse;
import com.join.bff.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody ProductCreationDTO dto) {
        return service.saveProduct(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id,
                                              @RequestBody ProductCreationDTO dto) {
        return service.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return service.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Optional<Long> categoryId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "name,asc") String[] sort) {
        return service.findAll(categoryId, page, size, sort);
    }
}