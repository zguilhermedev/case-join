package com.join.bff.domain.service;

import com.join.bff.application.dto.request.ProductCreationDTO;
import com.join.bff.application.dto.response.ResponseDTO;
import com.join.bff.domain.dto.request.ProductsRequest;
import com.join.bff.domain.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import java.util.Optional;

@Service
public class ProductService {

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public ProductService(RestTemplate restTemplate,
                          @Value("${services.product.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<ResponseDTO> saveProduct(ProductCreationDTO dto) {
        return restTemplate.postForEntity(baseUrl + "/product", new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> updateProduct(Long id, ProductCreationDTO dto) {
        return restTemplate.exchange(baseUrl + "/product/"  + id, HttpMethod.PUT, new HttpEntity<>(dto), ResponseDTO.class);
    }

    public ResponseEntity<ResponseDTO> deleteProduct(Long id) {
        return restTemplate.exchange(baseUrl + "/product/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, ResponseDTO.class);
    }

    public ResponseEntity<ProductResponse> findById(Long id) {
        return restTemplate.exchange(baseUrl + "/product/" + id, HttpMethod.GET, HttpEntity.EMPTY, ProductResponse.class);
    }

    public ResponseEntity<?> findAll(Optional<Long> categoryId, int page, int size, String[] sort) {
        String sortParam = String.join(",", sort);
        StringBuilder url = new StringBuilder(String.format("%s/product?page=%d&size=%d&sort=%s", baseUrl, page, size, sortParam));
        categoryId.ifPresent(uid -> url.append("&categoryId=").append(uid));
        return restTemplate.exchange(url.toString(), HttpMethod.GET, HttpEntity.EMPTY, Object.class);
    }

    public List<ProductResponse> getAllByIds(List<Long> ids) {
        String url = baseUrl + "/product/batch";
        ParameterizedTypeReference<List<ProductResponse>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new ProductsRequest(ids)), responseType);

        return response.getBody();
    }
}
