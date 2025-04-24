package com.join.bff.application.controller;

import com.join.bff.application.dto.request.CategoryCreationDTO;
import com.join.bff.application.dto.response.CategoryDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import com.join.bff.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody CategoryCreationDTO dto) {
        return categoryService.saveCategory(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@RequestBody CategoryDTO dto) {
        return categoryService.updateCategory(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Optional<String> name,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "name,asc") String[] sort) {
        return categoryService.findAll(name, page, size, sort);
    }
}
