package com.crediticio.auth.integration;

/**
 * Credenciales ficticias usadas exclusivamente en tests de integracion.
 * NO son secretos reales — existen solo en la BD H2 en memoria de cada test run.
 */
public final class TestCredentials {

    public static final String ADMIN_EMAIL = "admin@test.com";
    public static final String ADMIN_PASSWORD = "admin123"; // NOSONAR — test-only
    public static final String ADMIN_NOMBRE = "Admin Test";

    public static final String ANALISTA_EMAIL = "analista@test.com";
    public static final String ANALISTA_PASSWORD = "analista123"; // NOSONAR — test-only
    public static final String ANALISTA_NOMBRE = "Analista Test";

    public static final String NEW_USER_PASSWORD = "Segura2026!xyz"; // NOSONAR — test-only

    private TestCredentials() {
    }
}
