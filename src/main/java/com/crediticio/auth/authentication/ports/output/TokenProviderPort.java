package com.crediticio.auth.authentication.ports.output;

public interface TokenProviderPort {

    String generateToken(String email, String rol);

    String getEmailFromToken(String token);

    String getRolFromToken(String token);

    boolean isTokenValid(String token);
}
