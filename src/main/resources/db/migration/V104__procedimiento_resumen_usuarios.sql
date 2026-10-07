-- Procedimiento almacenado: resumen de usuarios agrupados por rol.
-- Consulta no trivial con LEFT JOIN, GROUP BY, y conteo condicional (FILTER).
CREATE OR REPLACE FUNCTION sp_resumen_usuarios_por_rol()
RETURNS TABLE (
    rol_nombre      VARCHAR,
    total_usuarios  BIGINT,
    activos         BIGINT,
    inactivos       BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.nombre,
        COUNT(u.id_usuario)                                       AS total_usuarios,
        COUNT(u.id_usuario) FILTER (WHERE u.activo = TRUE)        AS activos,
        COUNT(u.id_usuario) FILTER (WHERE u.activo = FALSE)       AS inactivos
    FROM roles r
    LEFT JOIN usuarios u ON r.id_rol = u.id_rol
    GROUP BY r.nombre
    ORDER BY total_usuarios DESC;
END;
$$ LANGUAGE plpgsql;
