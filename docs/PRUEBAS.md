# Pruebas — crediticio-auth

## Ejecutar la suite completa

```bash
./mvnw -B clean test        # Linux/Mac
.\mvnw.cmd -B clean test    # Windows
```

La suite usa **H2 en memoria** y un par RSA efimero generado por las pruebas; no requiere PostgreSQL, Supabase ni claves locales. El perfil `test` (`@ActiveProfiles("test")`) se activa automaticamente.

Para ejecutar una clase concreta:

```bash
.\mvnw.cmd -Dtest=AuthFlowIntegrationTest -B test
```

## Clases de test

| Clase | Tipo | Tests | Que cubre |
|---|---|---|---|
| `UserServiceTest` | Unitario | 24 | createUser (normalizacion, rol permitido, carrera), getAllUsers, getUserById, toggleStatus, changePassword, updateRole |
| `AuthServiceTest` | Unitario | 20 | login, refresh (rotacion, reutilizacion, vencido, inactivo, carrera), logout, validate |
| `JwtTokenProviderTest` | Unitario | 12 | RS256 claims, iss/aud, vencido, alterado, rechazo HMAC, refresh opaco/hash, limites de configuracion |
| `AdminBootstrapRunnerTest` | Unitario | 4 | Primer administrador por variables de entorno |
| `HttpErrorHandlingIntegrationTest` | Integracion (servidor HTTP real) | 12 | Errores de entrada 400 (JSON mal formado, tipos), UTF-8 en 401/403, `idRol` inexistente, refresh invalido 401 |
| `AuthFlowIntegrationTest` | Integracion | 49 | Login, CRUD, RBAC, cambio contrasena y rol, refresh/logout, concurrencia, usuarios desactivados, auto-desactivacion |
| `UserRoleIntegrationTest` | Integracion | 7 | Cambio de rol: RBAC, rol valido/invalido, usuario inexistente |
| `RegressionTest` | Integracion (regresion) | 5 | Regresiones de errores corregidos en iteraciones anteriores |
| `AuthApplicationTests` | Contexto | 1 | Spring Boot context loads |

**Total: 134 tests.**

## Credenciales de test

Las credenciales usadas en tests de integracion estan centralizadas en `TestCredentials.java` (`src/test/java/.../integration/TestCredentials.java`). Son valores ficticios que solo existen en la BD H2 en memoria de cada ejecucion. Estan centralizadas para evitar alertas de GitGuardian por cadenas que parecen secretos.

## Cobertura (JaCoCo)

El reporte de cobertura se genera automaticamente con cada `mvnw test`:

```
target/site/jacoco/jacoco.xml    # XML (consumido por SonarCloud)
target/site/jacoco/index.html    # HTML navegable
```

El pipeline de CI sube estos reportes como artefacto (`test-reports-*`) con retencion de 7 dias.

## Notas

- JDK requerido: **21** (`JAVA_HOME` apuntando a JDK 21).
- Los tests de integracion arrancan un servidor HTTP real con `@SpringBootTest(webEnvironment = RANDOM_PORT)`.
- `@ActiveProfiles("test")` activa `application-test.yml` (H2, RSA efimero).
- El par RSA de test se genera en cada ejecucion; no se usan las claves de `keys/`.
