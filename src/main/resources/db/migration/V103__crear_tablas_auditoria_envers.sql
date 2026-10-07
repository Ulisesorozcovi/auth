-- Tabla de revisiones de Hibernate Envers.
-- Cada operación de escritura (INSERT, UPDATE, DELETE) sobre una entidad
-- auditada genera una fila aquí con el timestamp y el usuario que la ejecutó.
CREATE TABLE revinfo (
    rev         SERIAL       PRIMARY KEY,
    revtstmp    BIGINT       NOT NULL,
    username    VARCHAR(255)
);

-- Tabla de auditoría para la entidad usuarios.
-- Almacena una copia de cada versión de la fila modificada.
-- revtype: 0 = INSERT, 1 = UPDATE, 2 = DELETE.
CREATE TABLE usuarios_aud (
    id_usuario          INTEGER   NOT NULL,
    rev                 INTEGER   NOT NULL REFERENCES revinfo(rev),
    revtype             SMALLINT,
    nombre              VARCHAR(150),
    email               VARCHAR(255),
    password_hash       VARCHAR(255),
    id_rol              INTEGER,
    activo              BOOLEAN,
    fecha_creacion      TIMESTAMP,
    fecha_modificacion  TIMESTAMP,
    PRIMARY KEY (id_usuario, rev)
);

-- Tabla de auditoría para la entidad roles.
CREATE TABLE roles_aud (
    id_rol    INTEGER   NOT NULL,
    rev       INTEGER   NOT NULL REFERENCES revinfo(rev),
    revtype   SMALLINT,
    nombre    VARCHAR(50),
    PRIMARY KEY (id_rol, rev)
);

CREATE INDEX idx_usuarios_aud_rev ON usuarios_aud (rev);
CREATE INDEX idx_roles_aud_rev ON roles_aud (rev);
