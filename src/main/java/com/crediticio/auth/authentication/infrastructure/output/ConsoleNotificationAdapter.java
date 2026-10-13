package com.crediticio.auth.authentication.infrastructure.output;

import com.crediticio.auth.authentication.ports.output.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleNotificationAdapter implements NotificationPort {

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        log.info("========== SIMULACION DE EMAIL ==========");
        log.info("Para: {}", email);
        log.info("Asunto: Recuperación de contraseña - Crediticio");
        log.info("Token de recuperación: {}", resetToken);
        log.info("Enlace: http://localhost:3000/reset-password?token={}", resetToken);
        log.info("Este token expira en 30 minutos.");
        log.info("==========================================");
    }
}
