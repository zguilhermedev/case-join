package com.join.auth.application.controller;

import com.join.auth.application.dto.request.AuthRequest;
import com.join.auth.application.dto.response.AuthResponse;
import com.join.auth.application.dto.response.UserDTO;
import com.join.auth.application.dto.response.ValidateTokenResponse;
import com.join.auth.domain.entity.UserEntity;
import com.join.auth.domain.service.UserService;
import com.join.auth.infrastructure.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        UserEntity user = userService.findByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwt.generateToken(user);
        AuthResponse response = new AuthResponse(token, new UserDTO(user.getId(), user.getUsername(), user.getRole()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validate(@RequestHeader("Authorization") String bearerToken) {
        ValidateTokenResponse response = new ValidateTokenResponse(jwt.validate(bearerToken.replace("Bearer ", "")));
        return ResponseEntity.ok(response);
    }
}
