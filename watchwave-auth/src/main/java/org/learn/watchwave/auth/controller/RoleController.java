package org.learn.watchwave.auth.controller;

import jakarta.validation.Valid;
import org.learn.watchwave.auth.dto.request.ProcessRoleRequest;
import org.learn.watchwave.auth.dto.request.RoleChangeRequest;
import org.learn.watchwave.auth.model.entity.RoleChangeRequestEntity;
import org.learn.watchwave.auth.service.interfaces.RoleService;
import org.learn.watchwave.auth.service.interfaces.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private final TokenService tokenService;

    public RoleController(RoleService roleService, TokenService tokenService) {
        this.roleService = roleService;
        this.tokenService = tokenService;
    }

    @PostMapping("/request-change")
    @PreAuthorize("hasRole('USER') or hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<String> requestRoleChange(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RoleChangeRequest request) {

        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        roleService.requestRoleChange(userId, request.getRequestedRole());
        return ResponseEntity.ok("Role change request submitted");
    }

    @PostMapping("/request-admin")  // Renamed from request-change
    @PreAuthorize("hasRole('CREATOR')")  // Only creators can request admin
    public ResponseEntity<String> requestAdminRole(@RequestHeader("Authorization") String authHeader) {
        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        roleService.requestRoleChange(userId, "ADMIN");
        return ResponseEntity.ok("Admin role request submitted for approval");
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleChangeRequestEntity>> getPendingRoleRequests() {
        List<RoleChangeRequestEntity> requests = roleService.getPendingRoleRequests();
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/requests/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> processRoleRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody ProcessRoleRequest request) {

        roleService.processRoleRequest(requestId, request.getStatus());
        return ResponseEntity.ok("Role request processed");
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('USER') or hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<RoleChangeRequestEntity>> getMyRoleRequests(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        List<RoleChangeRequestEntity> requests = roleService.getUserRoleRequests(userId);
        return ResponseEntity.ok(requests);
    }
}
