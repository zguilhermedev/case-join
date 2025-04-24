package com.join.auth.domain.service;

import com.join.auth.application.dto.request.UserCreationDTO;
import com.join.auth.application.dto.response.UserDTO;
import com.join.auth.application.mapper.UserMapper;
import com.join.auth.domain.entity.UserEntity;
import com.join.auth.domain.exception.NotFoundException;
import com.join.auth.infrastructure.repository.UserRepository;
import com.join.auth.infrastructure.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserEntity findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserDTO register(UserCreationDTO dto) {
        UserEntity entity = mapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapper.toDTO(repository.save(entity));
    }

    public UserDTO findById(Long id) {
        return mapper.toDTO(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
