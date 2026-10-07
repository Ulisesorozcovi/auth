package com.crediticio.auth.authentication.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales de autenticacion")
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(description = "Correo electronico del usuario", example = "admin@crediticio.com")
    private String email;

    @NotBlank
    @Schema(description = "Contrasena del usuario", example = "Admin123!")
    private String password;
}
