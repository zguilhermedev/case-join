package com.join.auth.application.mapper;

import com.join.auth.application.dto.request.UserCreationDTO;
import com.join.auth.application.dto.response.UserDTO;
import com.join.auth.domain.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(UserEntity entity);
    UserEntity toEntity(UserCreationDTO dto);
}
