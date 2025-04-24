package com.join.bff.application.dto.request;

import com.join.bff.application.dto.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {
    private String username;
    private String password;
    private Role role;
}