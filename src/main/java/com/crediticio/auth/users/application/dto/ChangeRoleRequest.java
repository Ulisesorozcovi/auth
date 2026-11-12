package com.crediticio.auth.users.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para cambiar el rol de un usuario")
public class ChangeRoleRequest {

    @NotBlank
    @Schema(description = "Nombre del nuevo rol", example = "ANALISTA_CREDITO")
    private String rolNombre;
}
