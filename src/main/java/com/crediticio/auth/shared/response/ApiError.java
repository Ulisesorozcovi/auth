package com.crediticio.auth.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de error estandarizada de la API")
public class ApiError {

    @Schema(description = "Codigo de error interno", example = "USER_NOT_FOUND")
    private final String errorCode;

    @Schema(description = "Mensaje legible del error", example = "Usuario no encontrado con id: 99")
    private final String message;

    @Schema(description = "Detalle de errores por campo (solo en errores de validacion)")
    private final Map<String, String> details;

    @Schema(description = "Identificador unico para rastreo del error", example = "550e8400-e29b-41d4-a716-446655440000")
    private final String traceId;

    @Builder.Default
    @Schema(description = "Timestamp del error")
    private final LocalDateTime timestamp = LocalDateTime.now();
}
