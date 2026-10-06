# API de Autenticación y Gestión de Usuarios

Backend de identidad, roles y tokens del **Sistema de Scoring de Riesgo Crediticio**.

Gestiona el ciclo de vida de usuarios (registro, consulta, activación/desactivación), asignación de roles y autenticación mediante JWT. Expone endpoints REST consumidos por el frontend y consultados por el Motor de Scoring para validar identidad y permisos.

## Ecosistema

| Componente | Repositorio | Puerto |
|---|---|---|
| Motor de Scoring | `motor-scoring-crediticio` | `8080` |
| **API de Autenticación** (este repo) | `crediticio.auth` | `8081` |
| Frontend | `credit-compass` | `3000` |

Los tres comparten la misma base de datos PostgreSQL (`db_scoring`). Este backend administra las tablas `usuarios` y `roles`; el Motor de Scoring administra solicitantes, reglas y evaluaciones.

## Tecnologías

- Java 21
- Spring Boot 3.3.5 (Web MVC, Data JPA, Security, Validation, Actuator)
- Maven (con Maven Wrapper)
- PostgreSQL + Flyway (migraciones `V100+`)
- JWT (`jjwt` 0.12.6) — autenticación stateless
- BCrypt — hashing de contraseñas
- springdoc-openapi 2.6.0 (Swagger UI)
- H2 (perfil `test`)
- Lombok

## Arquitectura

**Monolito modular** con **arquitectura hexagonal** (Ports & Adapters) interna por módulo.

```
com.crediticio.auth/
├── authentication/          # Login, JWT, refresh, validate
│   ├── application/         # AuthService + DTOs
│   ├── domain/
│   ├── ports/
│   │   ├── input/           # AuthInputPort
│   │   └── output/          # TokenProviderPort, PasswordEncoderPort
│   └── infrastructure/
│       ├── input/           # AuthController
│       └── output/          # JwtTokenProvider, BcryptPasswordEncoderAdapter
├── users/                   # CRUD de usuarios
│   ├── application/         # UserService + DTOs
│   ├── domain/              # Usuario (entidad pura)
│   ├── ports/
│   │   ├── input/           # CreateUser, GetUser, ToggleStatus
│   │   └── output/          # UserRepositoryPort
│   └── infrastructure/
│       ├── input/           # UsuarioController
│       └── output/          # JPA entity, repository, adapter
├── roles/                   # Catálogo de roles
│   ├── domain/              # Rol (entidad pura)
│   ├── ports/output/        # RolRepositoryPort
│   └── infrastructure/output/
├── config/                  # SecurityConfig, JwtFilter, CORS, OpenAPI
└── shared/                  # Excepciones, ApiError, GlobalExceptionHandler
```

**Principio clave:** el dominio no importa Spring, JPA ni ningún framework. La aplicación orquesta a través de puertos (interfaces). La infraestructura implementa los adaptadores concretos.

## Requisitos previos

- JDK 21
- PostgreSQL con la base de datos `db_scoring` creada

## Configuración

### Variables de entorno

| Variable | Descripción | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:postgresql://localhost:5432/db_scoring` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña BD | — |
| `JWT_SECRET` | Clave para firmar tokens (mín. 256 bits) | — |
| `JWT_EXPIRATION_MS` | Expiración del token en ms | `3600000` (1 hora) |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos (separados por coma) | `http://localhost:3000` |
| `SERVER_PORT` | Puerto del servidor | `8081` |

### Ejecución local

```bash
# Clonar
git clone https://github.com/Ulisesorozcovi/crediticio.auth.git
cd crediticio.auth

# Configurar variables (ejemplo)
export SPRING_DATASOURCE_PASSWORD=tu_password
export JWT_SECRET=una-clave-secreta-de-al-menos-32-caracteres-para-256-bits

# Ejecutar
./mvnw spring-boot:run        # Linux/Mac
.\mvnw.cmd spring-boot:run    # Windows
```

La aplicación arranca en `http://localhost:8081`. Flyway ejecuta las migraciones automáticamente al iniciar.

### Swagger UI

Disponible en `http://localhost:8081/swagger-ui.html` — incluye esquema Bearer para autenticación con JWT.

## Endpoints

### Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/login` | Login con email y contraseña → JWT + rol | Público |
| `POST` | `/refresh` | Renueva un token válido | ADMIN, ANALISTA |
| `GET` | `/validate` | Valida token y retorna identidad + rol | ADMIN, ANALISTA |

### Usuarios (`/api/v1/usuarios`)

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/` | Crea usuario con rol asignado | ADMIN |
| `GET` | `/` | Lista todos los usuarios | ADMIN |
| `GET` | `/{id}` | Detalle de un usuario | ADMIN (cualquiera), ANALISTA (solo propio) |
| `PATCH` | `/{id}/estado` | Activa o desactiva un usuario | ADMIN |

### Respuesta de error

Todas las respuestas de error siguen el formato `ApiError`:

```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Error de validación en los datos de entrada",
  "details": { "email": "must not be blank" },
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-10-06T15:30:00"
}
```

## Modelo de datos

### `roles`

| Columna | Tipo | Restricción |
|---|---|---|
| `id_rol` | SERIAL | PK |
| `nombre` | VARCHAR(50) | UNIQUE NOT NULL |

Seed: `ADMIN`, `ANALISTA`.

### `usuarios`

| Columna | Tipo | Restricción |
|---|---|---|
| `id_usuario` | SERIAL | PK |
| `nombre` | VARCHAR(150) | NOT NULL |
| `email` | VARCHAR(255) | UNIQUE NOT NULL |
| `password_hash` | VARCHAR(255) | NOT NULL |
| `id_rol` | INTEGER | FK → `roles.id_rol` |
| `activo` | BOOLEAN | DEFAULT TRUE |
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| `fecha_modificacion` | TIMESTAMP | Nullable |

Las migraciones Flyway usan prefijo `V100+` para no colisionar con las del Motor de Scoring (`V1-V99`).

## Flujo de autenticación

```
┌──────────┐     POST /auth/login      ┌──────────────┐
│ Frontend │ ──────────────────────────→│ Auth Backend │
│          │ ←────────────────────────── │  (este repo) │
│          │     { token, rol }         └──────────────┘
│          │
│          │  Authorization: Bearer <token>
│          │ ──────────────────────────→ Auth Backend | Motor de Scoring
└──────────┘
```

1. El frontend envía `POST /auth/login` → recibe el JWT.
2. Incluye el JWT como `Authorization: Bearer <token>` en peticiones a ambos backends.
3. El Motor de Scoring valida el token de forma autónoma (misma `JWT_SECRET`) o vía `GET /auth/validate`.

## Pruebas

```bash
./mvnw clean test        # Linux/Mac
.\mvnw.cmd clean test    # Windows
```

- **Unitarias** (Mockito): `UserService` (8), `AuthService` (8), `JwtTokenProvider` (7)
- **Integración** (MockMvc + H2): flujo completo login→token→RBAC (16)
- **Contexto**: `AuthApplicationTests` (1)

Total: **39 tests**.

## Coordinación con el Motor de Scoring

- Comparten la misma BD (`db_scoring`) pero **no se llaman entre sí directamente**.
- Cada backend gestiona sus propias tablas con rangos de migración Flyway diferenciados.
- El frontend orquesta las peticiones a ambos backends.
