package com.crediticio.auth.roles.infrastructure.output;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolJpaRepository extends JpaRepository<RolJpaEntity, Long> {

    Optional<RolJpaEntity> findByNombre(String nombre);
}
