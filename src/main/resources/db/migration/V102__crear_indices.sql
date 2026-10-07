-- Índice en la FK id_rol de usuarios.
-- PostgreSQL NO crea índice automático en columnas FK (sí lo hace en PK y UNIQUE).
-- Justificación: cada consulta de usuario hace JOIN con roles (@ManyToOne).
-- Sin este índice, el join escanea secuencialmente la columna id_rol.
CREATE INDEX idx_usuarios_id_rol ON usuarios (id_rol);

-- Índice en el campo activo de usuarios.
-- Justificación: filtrar usuarios activos/inactivos es una operación frecuente
-- en la gestión del sistema (listar solo activos, verificar estado en login).
-- Aunque la cardinalidad es baja (true/false), PostgreSQL puede usar un index scan
-- cuando la distribución es desbalanceada (pocos inactivos entre muchos activos).
CREATE INDEX idx_usuarios_activo ON usuarios (activo);
