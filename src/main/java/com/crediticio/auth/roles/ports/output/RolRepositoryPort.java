package com.crediticio.auth.roles.ports.output;

import com.crediticio.auth.roles.domain.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepositoryPort {

    Optional<Rol> findById(Long id);

    Optional<Rol> findByNombre(String nombre);

    List<Rol> findAll();
}
