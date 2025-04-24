package com.join.order_service.application.controller;

import com.join.order_service.application.dto.request.OrderCreationDTO;
import com.join.order_service.application.dto.response.OrderDTO;
import com.join.order_service.application.dto.response.ResponseDTO;
import com.join.order_service.domain.exception.NotFoundException;
import com.join.order_service.domain.service.OrderService;
import com.join.order_service.infrastructure.util.Constants;
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

import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody OrderCreationDTO dto) {
        try {
            service.createOrder(dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Order saved successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, "Tente novamente mais tarde."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody OrderCreationDTO dto) {
        try {
            service.updateOrder(id, dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Order updated successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, "Tente novamente mais tarde."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Order deleted successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, "Tente novamente mais tarde."));
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
    public Page<OrderDTO> findAll(
            @RequestParam(required = false) Optional<Long> userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return service.findAll(userId, pageable);
    }
}
