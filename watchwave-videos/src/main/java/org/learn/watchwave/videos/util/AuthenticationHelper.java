package org.learn.watchwave.videos.util;

import org.learn.watchwave.auth.service.jwt.JwtService;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuthenticationHelper {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public UUID extractUserId(Authentication authentication) {
        String token = extractTokenFromAuthentication(authentication);
        return jwtService.extractUserId(token);
    }

    public String extractUsername(Authentication authentication) {
        String token = extractTokenFromAuthentication(authentication);
        return jwtService.extractUsername(token);
    }

    public String extractUserRole(Authentication authentication) {
        // Use Spring Security authorities (which come from your existing auth system)
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .map(role -> role.substring(5))
                .findFirst()
                .orElse("USER");
    }

    public List<String> extractUserRoles(Authentication authentication) {
        // Get all roles from Spring Security authorities
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .map(role -> role.substring(5))
                .collect(Collectors.toList());
    }

    public boolean hasRole(Authentication authentication, String role) {
        return extractUserRole(authentication).equals(role);
    }

    public boolean hasAnyRole(Authentication authentication, String... roles) {
        String userRole = extractUserRole(authentication);
        for (String role : roles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTokenValid(Authentication authentication) {
        try {
            String token = extractTokenFromAuthentication(authentication);
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    private String extractTokenFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is required");
        }

        Object credentials = authentication.getCredentials();
        if (credentials instanceof String) {
            return (String) credentials;
        }

        throw new IllegalArgumentException("No valid JWT token found in authentication");
    }
}
