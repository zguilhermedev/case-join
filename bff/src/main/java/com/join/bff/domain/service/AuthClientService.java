package com.join.bff.domain.service;

import com.join.bff.application.dto.request.AuthRequestDTO;
import com.join.bff.application.dto.response.AuthResponseDTO;
import com.join.bff.application.dto.response.ValidateTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthClientService {

    private final RestTemplate restTemplate;

    @Value("${services.user.base-url}")
    private String authServiceUrl;

    public ResponseEntity<AuthResponseDTO> login(AuthRequestDTO request) {
        String url = authServiceUrl + "/auth/login";
        return restTemplate.postForEntity(url, request, AuthResponseDTO.class);
    }

    public ResponseEntity<ValidateTokenResponse> validateToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = authServiceUrl + "/auth/validate";
        return restTemplate.exchange(url, HttpMethod.GET, entity, ValidateTokenResponse.class);
    }

}
