package com.crediticio.auth.authentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    private Long userId;
    private String email;
    private String rol;
}
