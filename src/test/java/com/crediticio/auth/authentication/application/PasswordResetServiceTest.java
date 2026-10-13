package com.crediticio.auth.authentication.application;

import com.crediticio.auth.authentication.application.dto.ForgotPasswordRequest;
import com.crediticio.auth.authentication.application.dto.ResetPasswordRequest;
import com.crediticio.auth.authentication.domain.PasswordResetToken;
import com.crediticio.auth.authentication.ports.output.NotificationPort;
import com.crediticio.auth.authentication.ports.output.PasswordEncoderPort;
import com.crediticio.auth.authentication.ports.output.PasswordResetTokenRepositoryPort;
import com.crediticio.auth.roles.domain.Rol;
import com.crediticio.auth.shared.exception.InvalidCredentialsException;
import com.crediticio.auth.users.domain.Usuario;
import com.crediticio.auth.users.ports.output.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordResetTokenRepositoryPort tokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private PasswordResetService service;

    private final Rol analistaRol = Rol.builder().id(2L).nombre("ANALISTA").build();

    private Usuario buildUsuario(String email, boolean activo) {
        return Usuario.builder()
                .id(1L).nombre("Test").email(email)
                .passwordHash("hash").rol(analistaRol)
                .activo(activo).fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void forgotPassword_existingActiveUser_createsTokenAndNotifies() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(buildUsuario("test@test.com", true)));
        when(tokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.forgotPassword(new ForgotPasswordRequest("test@test.com"));

        verify(tokenRepository).deleteByEmail("test@test.com");
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(notificationPort).sendPasswordResetEmail(eq("test@test.com"), anyString());
    }

    @Test
    void forgotPassword_nonExistentUser_doesNothing() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        service.forgotPassword(new ForgotPasswordRequest("noexiste@test.com"));

        verify(tokenRepository, never()).save(any());
        verify(notificationPort, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void forgotPassword_inactiveUser_doesNothing() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(buildUsuario("test@test.com", false)));

        service.forgotPassword(new ForgotPasswordRequest("test@test.com"));

        verify(tokenRepository, never()).save(any());
        verify(notificationPort, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_validToken_changesPassword() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L).token("abc-123").email("test@test.com")
                .expiresAt(LocalDateTime.now().plusMinutes(30)).used(false)
                .build();

        when(tokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(buildUsuario("test@test.com", true)));
        when(passwordEncoder.encode("NuevaClave1!")).thenReturn("newHash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.resetPassword(new ResetPasswordRequest("abc-123", "NuevaClave1!"));

        verify(passwordEncoder).encode("NuevaClave1!");
        verify(userRepository).save(any(Usuario.class));
        verify(tokenRepository).save(argThat(t -> t.isUsed()));
    }

    @Test
    void resetPassword_expiredToken_throws() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L).token("expired").email("test@test.com")
                .expiresAt(LocalDateTime.now().minusMinutes(1)).used(false)
                .build();

        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        assertThrows(InvalidCredentialsException.class,
                () -> service.resetPassword(new ResetPasswordRequest("expired", "NuevaClave1!")));
    }

    @Test
    void resetPassword_usedToken_throws() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L).token("used").email("test@test.com")
                .expiresAt(LocalDateTime.now().plusMinutes(30)).used(true)
                .build();

        when(tokenRepository.findByToken("used")).thenReturn(Optional.of(token));

        assertThrows(InvalidCredentialsException.class,
                () -> service.resetPassword(new ResetPasswordRequest("used", "NuevaClave1!")));
    }

    @Test
    void resetPassword_invalidToken_throws() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.resetPassword(new ResetPasswordRequest("invalid", "NuevaClave1!")));
    }
}
