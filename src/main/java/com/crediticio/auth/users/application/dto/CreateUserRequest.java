package com.crediticio.auth.users.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un nuevo usuario")
public class CreateUserRequest {

    @NotBlank
    @Size(max = 150)
    @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(description = "Correo electronico (unico en el sistema)", example = "juan.perez@ejemplo.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Debe contener al menos una mayuscula, una minuscula, un digito y un caracter especial (@$!%*?&)")
    @Schema(description = "Contrasena (8-72 caracteres, debe incluir mayuscula, minuscula, digito y caracter especial)", example = "Segura123!")
    private String password;

    @NotNull
    @Schema(description = "ID del rol a asignar (1=ADMIN, 2=ANALISTA)", example = "2")
    private Long idRol;
}
