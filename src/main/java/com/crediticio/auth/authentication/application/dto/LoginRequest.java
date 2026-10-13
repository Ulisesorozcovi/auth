package com.crediticio.auth.authentication.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255)
    @Schema(description = "Correo electronico del usuario", example = "usuario@ejemplo.com")
    private String email;

    @NotBlank
    @Size(max = 72)
    @Schema(description = "Contrasena del usuario", example = "MiClave123!")
    private String password;
}
