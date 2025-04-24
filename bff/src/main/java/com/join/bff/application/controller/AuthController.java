package com.join.bff.application.controller;

import com.join.bff.application.dto.request.AuthRequestDTO;
import com.join.bff.application.dto.response.AuthResponseDTO;
import com.join.bff.application.dto.response.ValidateTokenResponse;
import com.join.bff.domain.service.AuthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthClientService authClientService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return authClientService.login(request);
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validate(@RequestHeader("Authorization") String bearerToken) {
        return authClientService.validateToken(bearerToken.replace("Bearer ", ""));
    }
}
