package org.learn.watchwave.auth.service.interfaces;

import java.util.UUID;

public interface TokenService {

    UUID extractUserIdFromToken(String authHeader);

    boolean isValidAuthHeader(String authHeader);
}
