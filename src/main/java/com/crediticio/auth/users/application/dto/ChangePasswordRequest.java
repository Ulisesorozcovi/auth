package com.crediticio.auth.users.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Solicitud de cambio de contrasena")
public class ChangePasswordRequest {

    @Schema(description = "Contrasena actual (obligatorio para usuarios no ADMIN que cambian su propia contrasena)", example = "MiClaveActual1!")
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Debe contener al menos una mayuscula, una minuscula, un digito y un caracter especial (@$!%*?&)")
    @Schema(description = "Nueva contrasena (8-72 caracteres, debe incluir mayuscula, minuscula, digito y caracter especial)", example = "NuevaClave123!")
    private String newPassword;
}
