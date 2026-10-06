package com.crediticio.auth.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String errorCode;
    private final String message;
    private final Map<String, String> details;
    private final String traceId;
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}
