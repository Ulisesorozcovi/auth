package com.crediticio.auth.roles.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Rol {

    private Long id;
    private String nombre;
}
