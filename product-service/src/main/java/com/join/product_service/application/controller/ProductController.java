package com.join.product_service.application.controller;

import com.join.product_service.application.dto.request.ProductCreationDTO;
import com.join.product_service.application.dto.request.ProductsRequest;
import com.join.product_service.application.dto.response.ProductDTO;
import com.join.product_service.application.dto.response.ResponseDTO;
import com.join.product_service.domain.exception.NotFoundException;
import com.join.product_service.domain.service.ProductService;
import com.join.product_service.infrastructure.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody ProductCreationDTO dto) {
        try {
            service.save(dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Product saved successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, Constants.ERROR_MSG));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody ProductCreationDTO dto) {
        try {
            service.update(id, dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Product updated successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, Constants.ERROR_MSG));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Product deleted successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, Constants.ERROR_MSG));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public Page<ProductDTO> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return service.findAll(categoryId, pageable);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(@RequestBody ProductsRequest dto) {
        List<ProductDTO> products = service.getProductsByIds(dto.getIds());
        return ResponseEntity.ok(products);
    }
}
