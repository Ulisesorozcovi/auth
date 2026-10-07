# ADR-001: Arquitectura Hexagonal (Ports & Adapters)

**Estado:** Aceptado  
**Fecha:** 2026-10-06  
**Autores:** Equipo de desarrollo

## Contexto

El backend de autenticación necesita una arquitectura que permita:

- Separar la lógica de negocio de los detalles de infraestructura (base de datos, framework web, proveedor de tokens).
- Facilitar las pruebas unitarias sin depender de Spring, JPA ni servicios externos.
- Cambiar componentes de infraestructura (por ejemplo, migrar de BCrypt a Argon2, o de jjwt a otra librería) sin modificar el dominio ni la aplicación.

Se evaluaron tres alternativas:

1. **Package by Layer** (controller → service → repository): simple pero acopla el dominio a la infraestructura.
2. **Clean Architecture** (Uncle Bob): similar a hexagonal pero con más capas (entities, use cases, interface adapters, frameworks), mayor complejidad para un backend CRUD.
3. **Hexagonal / Ports & Adapters**: separación clara dominio ↔ infraestructura mediante interfaces (ports), complejidad moderada.

## Decisión

Adoptamos **arquitectura hexagonal** con organización **Package by Feature**. Cada módulo de negocio (`users`, `roles`, `authentication`) contiene su propio `domain/`, `application/`, `ports/` e `infrastructure/`.

```
modulo/
├── domain/              # Entidades puras (sin framework)
├── application/         # Casos de uso + DTOs
├── ports/
│   ├── input/           # Interfaces de casos de uso
│   └── output/          # Interfaces de repositorios y servicios externos
└── infrastructure/
    ├── input/           # Controllers REST
    └── output/          # JPA adapters, JWT provider, BCrypt adapter
```

## Consecuencias

### Positivas

- El dominio (`Rol`, `Usuario`) no importa Spring, JPA ni Lombok de infraestructura — es Java puro con Lombok solo para boilerplate.
- Los servicios de aplicación dependen de puertos (interfaces), no de implementaciones concretas. Esto permite inyectar mocks en pruebas unitarias sin levantar contexto Spring.
- Cambiar el proveedor de tokens (de jjwt a otra librería) solo requiere un nuevo adaptador que implemente `TokenProviderPort`.
- Cada módulo es autocontenido: agregar un módulo nuevo no afecta los existentes.

### Negativas

- Más archivos por funcionalidad (port + adapter + entity JPA + entity dominio vs. un solo `@Entity` + `@Service`).
- Requiere disciplina para no violar la regla de dependencia (dominio nunca importa infraestructura).
- Para un CRUD simple, la separación puede parecer excesiva. Se justifica por la necesidad de intercambiar componentes de seguridad (tokens, hashing).
