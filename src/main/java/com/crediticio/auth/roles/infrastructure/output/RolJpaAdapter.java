package com.crediticio.auth.roles.infrastructure.output;

import com.crediticio.auth.roles.domain.Rol;
import com.crediticio.auth.roles.ports.output.RolRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RolJpaAdapter implements RolRepositoryPort {

    private final RolJpaRepository rolJpaRepository;

    @Override
    public Optional<Rol> findById(Long id) {
        return rolJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) {
        return rolJpaRepository.findByNombre(nombre).map(this::toDomain);
    }

    @Override
    public List<Rol> findAll() {
        return rolJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Rol toDomain(RolJpaEntity entity) {
        return Rol.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }
}
