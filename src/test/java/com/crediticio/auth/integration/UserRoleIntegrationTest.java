package com.crediticio.auth.integration;

import static com.crediticio.auth.integration.TestCredentials.*;

import com.crediticio.auth.TestRsaKeyInitializer;
import com.crediticio.auth.authentication.ports.output.PasswordEncoderPort;
import com.crediticio.auth.roles.infrastructure.output.RolJpaEntity;
import com.crediticio.auth.roles.infrastructure.output.RolJpaRepository;
import com.crediticio.auth.users.infrastructure.output.UsuarioJpaEntity;
import com.crediticio.auth.users.infrastructure.output.UsuarioJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = TestRsaKeyInitializer.class)
class UserRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RolJpaRepository rolRepository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private PasswordEncoderPort passwordEncoder;

    @Autowired
    private com.crediticio.auth.authentication.infrastructure.output.RefreshTokenJpaRepository refreshTokenRepository;

    private RolJpaEntity adminRol;
    private RolJpaEntity analistaRol;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        adminRol = rolRepository.save(new RolJpaEntity(null, "ADMINISTRADOR_RIESGO"));
        analistaRol = rolRepository.save(new RolJpaEntity(null, "ANALISTA_CREDITO"));

        UsuarioJpaEntity admin = new UsuarioJpaEntity();
        admin.setNombre(ADMIN_NOMBRE);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRol(adminRol);
        admin.setActivo(true);
        usuarioRepository.save(admin);

        UsuarioJpaEntity analista = new UsuarioJpaEntity();
        analista.setNombre(ANALISTA_NOMBRE);
        analista.setEmail(ANALISTA_EMAIL);
        analista.setPasswordHash(passwordEncoder.encode(ANALISTA_PASSWORD));
        analista.setRol(analistaRol);
        analista.setActivo(true);
        usuarioRepository.save(analista);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private JsonNode login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }

    @Test
    void fullFlow_ChangeRole_ThenNewJwtReflectsNewRole() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long analistaId = usuarioRepository.findByEmail(ANALISTA_EMAIL).get().getId();

        JsonNode loginBefore = login(ANALISTA_EMAIL, ANALISTA_PASSWORD);
        org.junit.jupiter.api.Assertions.assertEquals("ANALISTA_CREDITO", loginBefore.get("rol").asText());

        mockMvc.perform(patch("/api/v1/usuarios/" + analistaId + "/rol")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rolNombre\":\"ADMINISTRADOR_RIESGO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rolNombre").value("ADMINISTRADOR_RIESGO"));

        JsonNode loginAfter = login(ANALISTA_EMAIL, ANALISTA_PASSWORD);
        org.junit.jupiter.api.Assertions.assertEquals("ADMINISTRADOR_RIESGO", loginAfter.get("rol").asText());
    }

    @Test
    void changeRole_asAnalista_returns403() throws Exception {
        String analistaToken = loginAndGetToken(ANALISTA_EMAIL, ANALISTA_PASSWORD);
        Long adminId = usuarioRepository.findByEmail(ADMIN_EMAIL).get().getId();

        mockMvc.perform(patch("/api/v1/usuarios/" + adminId + "/rol")
                        .header("Authorization", "Bearer " + analistaToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rolNombre\":\"ANALISTA_CREDITO\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeRole_invalidRolNombre_returns400() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long analistaId = usuarioRepository.findByEmail(ANALISTA_EMAIL).get().getId();

        mockMvc.perform(patch("/api/v1/usuarios/" + analistaId + "/rol")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rolNombre\":\"SUPERADMIN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeRole_userNotFound_returns404() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(patch("/api/v1/usuarios/99999/rol")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rolNombre\":\"ANALISTA_CREDITO\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeRole_blankRolNombre_returns400() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long analistaId = usuarioRepository.findByEmail(ANALISTA_EMAIL).get().getId();

        mockMvc.perform(patch("/api/v1/usuarios/" + analistaId + "/rol")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rolNombre\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void selfDeactivation_returns400() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long adminId = usuarioRepository.findByEmail(ADMIN_EMAIL).get().getId();

        mockMvc.perform(patch("/api/v1/usuarios/" + adminId + "/estado")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("SELF_DEACTIVATION"));
    }

    @Test
    void fullFlow_DeactivateUser_ThenLoginFails() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long analistaId = usuarioRepository.findByEmail(ANALISTA_EMAIL).get().getId();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ANALISTA_EMAIL + "\",\"password\":\"" + ANALISTA_PASSWORD + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/usuarios/" + analistaId + "/estado")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ANALISTA_EMAIL + "\",\"password\":\"" + ANALISTA_PASSWORD + "\"}"))
                .andExpect(status().isUnauthorized());
    }
}
