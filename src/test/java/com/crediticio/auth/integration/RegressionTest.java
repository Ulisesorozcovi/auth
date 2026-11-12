package com.crediticio.auth.integration;

import static com.crediticio.auth.integration.TestCredentials.*;

import com.crediticio.auth.TestRsaKeyInitializer;
import com.crediticio.auth.authentication.ports.output.PasswordEncoderPort;
import com.crediticio.auth.roles.infrastructure.output.RolJpaEntity;
import com.crediticio.auth.roles.infrastructure.output.RolJpaRepository;
import com.crediticio.auth.users.infrastructure.output.UsuarioJpaEntity;
import com.crediticio.auth.users.infrastructure.output.UsuarioJpaRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = TestRsaKeyInitializer.class)
class RegressionTest {

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
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void existingLoginFlowStillWorksForActiveUsers() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ADMIN_EMAIL + "\",\"password\":\"" + ADMIN_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR_RIESGO"));
    }

    @Test
    void existingUserCreationStillWorks() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(post("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nuevo\",\"email\":\"nuevo@test.com\",\"password\":\"" + NEW_USER_PASSWORD + "\",\"idRol\":" + analistaRol.getId() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.rolNombre").value("ANALISTA_CREDITO"));

        UsuarioJpaEntity nuevo = usuarioRepository.findByEmail("nuevo@test.com").orElseThrow();
        assertTrue(nuevo.getActivo());
    }

    @Test
    void existingToggleStatusEndpointStillWorks() throws Exception {
        UsuarioJpaEntity analista = new UsuarioJpaEntity();
        analista.setNombre(ANALISTA_NOMBRE);
        analista.setEmail("regresion@test.com");
        analista.setPasswordHash(passwordEncoder.encode(ANALISTA_PASSWORD));
        analista.setRol(analistaRol);
        analista.setActivo(true);
        usuarioRepository.save(analista);

        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long analistaId = usuarioRepository.findByEmail("regresion@test.com").get().getId();

        mockMvc.perform(patch("/api/v1/usuarios/" + analistaId + "/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void existingEndpointsDontBreak_listUsers() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(get("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void existingEndpointsDontBreak_getUserById() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        Long adminId = usuarioRepository.findByEmail(ADMIN_EMAIL).get().getId();

        mockMvc.perform(get("/api/v1/usuarios/" + adminId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(ADMIN_EMAIL));
    }
}
