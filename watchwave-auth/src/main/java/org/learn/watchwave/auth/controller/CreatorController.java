package org.learn.watchwave.auth.controller;

import org.learn.watchwave.auth.service.interfaces.RoleService;
import org.learn.watchwave.auth.service.interfaces.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/creator")
public class CreatorController {

    private final RoleService roleService;
    private final TokenService tokenService;

    public CreatorController(RoleService roleService, TokenService tokenService) {
        this.roleService = roleService;
        this.tokenService = tokenService;
    }

    @PostMapping("/upgrade")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> upgradeToCreator(@RequestHeader("Authorization") String authHeader) {
        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        roleService.upgradeToCreator(userId);
        return ResponseEntity.ok("Successfully upgraded to Creator role");
    }
}
