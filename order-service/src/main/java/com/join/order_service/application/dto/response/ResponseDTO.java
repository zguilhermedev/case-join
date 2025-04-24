package com.join.order_service.application.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ResponseDTO {
    private String code;
    private String message;

    public ResponseDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
