package org.learn.watchwave.auth.service.interfaces;

import org.learn.watchwave.auth.dto.request.AuthRequest;
import org.learn.watchwave.auth.dto.RegisterRequest;
import org.learn.watchwave.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest authRequest);

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse refreshToken(String token);

    void logout(String token);

    boolean validateToken(String token);
}