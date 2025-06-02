package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.service.interfaces.TokenService;
import org.learn.watchwave.auth.service.jwt.JwtService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;

    public TokenServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public UUID extractUserIdFromToken(String authHeader) {
        if (!isValidAuthHeader(authHeader)) {
            throw new RuntimeException("Invalid authorization header");
        }

        String token = authHeader.substring(7);

        if (jwtService.isTokenValid(token)) {
            return jwtService.extractUserId(token);
        }

        throw new RuntimeException("Invalid or expired token");
    }

    @Override
    public boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }
}
