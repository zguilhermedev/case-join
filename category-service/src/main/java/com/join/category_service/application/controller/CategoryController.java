package com.join.category_service.application.controller;

import com.join.category_service.application.dto.request.CategoryCreationDTO;
import com.join.category_service.application.dto.response.CategoryDTO;
import com.join.category_service.application.dto.response.ResponseDTO;
import com.join.category_service.domain.exception.NotFoundException;
import com.join.category_service.domain.service.CategoryService;
import com.join.category_service.infrastructure.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody CategoryCreationDTO dto) {
        try {
            service.save(dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Category saved successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(Constants.ERROR, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(Constants.ERROR, "Tente novamente mais tarde."));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> update(@RequestBody CategoryDTO dto) {
        try {
            service.update(dto);
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Category updated successfully");
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
            ResponseDTO responseDTO = new ResponseDTO(Constants.SUCCESS,"Category deleted successfully");
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
    public Page<CategoryDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Sort sortObj = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        return service.findAll(name, pageable);
    }
}
