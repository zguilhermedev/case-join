package com.join.bff.domain.service;

import com.join.bff.application.dto.request.CategoryCreationDTO;
import com.join.bff.application.dto.response.CategoryDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final RestTemplate restTemplate;

    @Value("${services.category.base-url}")
    private String baseUrl;

    public ResponseEntity<ResponseDTO> saveCategory(CategoryCreationDTO dto) {
        return restTemplate.postForEntity(baseUrl + "/category", new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> updateCategory(CategoryDTO dto) {
        return restTemplate.exchange(baseUrl + "/category", HttpMethod.PUT, new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> deleteCategory(Long id) {
        return restTemplate.exchange(baseUrl + "/category/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, ResponseDTO.class);
    }

    public ResponseEntity<CategoryDTO> findById(Long id) {
        return restTemplate.exchange(baseUrl + "/category/" + id, HttpMethod.GET, HttpEntity.EMPTY, CategoryDTO.class);
    }

    public ResponseEntity<?> findAll(Optional<String> name, int page, int size, String[] sort) {
        String sortParam = String.join(",", sort);
        StringBuilder urlBuilder = new StringBuilder(String.format("%s/category?page=%d&size=%d&sort=%s", baseUrl, page, size, sortParam));

        name.filter(s -> !s.isBlank())
                .ifPresent(s -> urlBuilder.append("&name=").append(URLEncoder.encode(s, StandardCharsets.UTF_8)));

        return restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, HttpEntity.EMPTY, Object.class);
    }

}
