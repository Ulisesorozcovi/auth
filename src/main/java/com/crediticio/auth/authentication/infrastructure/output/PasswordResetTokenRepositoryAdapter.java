package com.crediticio.auth.authentication.infrastructure.output;

import com.crediticio.auth.authentication.domain.PasswordResetToken;
import com.crediticio.auth.authentication.ports.output.PasswordResetTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpaRepository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.setId(token.getId());
        entity.setToken(token.getToken());
        entity.setEmail(token.getEmail());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setUsed(token.isUsed());

        PasswordResetTokenJpaEntity saved = jpaRepository.save(entity);

        return PasswordResetToken.builder()
                .id(saved.getId())
                .token(saved.getToken())
                .email(saved.getEmail())
                .expiresAt(saved.getExpiresAt())
                .used(saved.isUsed())
                .build();
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(entity -> PasswordResetToken.builder()
                        .id(entity.getId())
                        .token(entity.getToken())
                        .email(entity.getEmail())
                        .expiresAt(entity.getExpiresAt())
                        .used(entity.isUsed())
                        .build());
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        jpaRepository.deleteByEmail(email);
    }
}
