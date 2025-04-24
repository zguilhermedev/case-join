package com.join.bff.domain.service;

import com.join.bff.application.dto.request.UserCreationDTO;
import com.join.bff.application.dto.response.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserClientService {

    private final RestTemplate restTemplate;

    @Value("${services.user.base-url}")
    private String userServiceUrl;

    public List<UserDTO> getAllUsers() {
        String url = userServiceUrl + "/user";
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(url, UserDTO[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public UserDTO getUserById(Long id) {
        String url = userServiceUrl + "/user/" + id;
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public UserDTO createUser(UserCreationDTO dto) {
        String url = userServiceUrl + "/user";
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, dto, UserDTO.class);
        return response.getBody();
    }

    public void deleteUser(Long id) {
        String url = userServiceUrl + "/user/" + id;
        restTemplate.delete(url);
    }

}
