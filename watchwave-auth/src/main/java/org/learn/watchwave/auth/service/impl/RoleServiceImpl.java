package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.model.entity.Role;
import org.learn.watchwave.auth.model.entity.RoleChangeRequestEntity;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.model.entity.UserRole;
import org.learn.watchwave.auth.model.id.UserRoleId;
import org.learn.watchwave.auth.repository.RoleChangeRequestRepository;
import org.learn.watchwave.auth.repository.RoleRepository;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.repository.UserRoleRepository;
import org.learn.watchwave.auth.service.interfaces.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleServiceImpl(RoleChangeRequestRepository roleChangeRequestRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository) {
        this.roleChangeRequestRepository = roleChangeRequestRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }
    @Override
    public void upgradeToCreator(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has CREATOR or ADMIN role
        boolean hasCreatorOrAdmin = user.getRoles().stream()
                .anyMatch(userRole -> {
                    String roleName = userRole.getRole().getRoleName();
                    return "CREATOR".equals(roleName) || "ADMIN".equals(roleName);
                });

        if (hasCreatorOrAdmin) {
            throw new RuntimeException("User already has Creator or Admin privileges");
        }

        // Assign CREATOR role directly
        assignRole(user, "CREATOR");

        System.out.println("✅ User " + user.getUsername() + " upgraded to CREATOR role");
    }

    @Override
    public void requestRoleChange(UUID userId, String requestedRole) {
        // Only allow admin role requests - creators can upgrade directly
        if (!"ADMIN".equals(requestedRole)) {
            throw new RuntimeException("Only ADMIN role requires approval. Use /api/creator/upgrade for Creator role.");
        }

        // Check if user already has pending admin request
        if (roleChangeRequestRepository.existsByUserIdAndStatus(userId, "PENDING")) {
            throw new RuntimeException("You already have a pending admin role request");
        }

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is at least a CREATOR
        boolean isCreator = user.getRoles().stream()
                .anyMatch(userRole -> "CREATOR".equals(userRole.getRole().getRoleName()));

        if (!isCreator) {
            throw new RuntimeException("You must be a Creator before requesting Admin role");
        }

        // Create admin role change request
        RoleChangeRequestEntity request = new RoleChangeRequestEntity();
        request.setUserId(userId);
        request.setRequestedRole(requestedRole);
        request.setStatus("PENDING");

        roleChangeRequestRepository.save(request);
    }

    @Override
    public List<RoleChangeRequestEntity> getPendingRoleRequests() {
        return roleChangeRequestRepository.findByStatus("PENDING");
    }

    @Override
    public void processRoleRequest(UUID requestId, String status) {
        RoleChangeRequestEntity request = roleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Role change request not found"));

        request.setStatus(status);
        request.setReviewedAt(Timestamp.from(Instant.now()));

        if ("APPROVED".equals(status)) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            assignRole(user, request.getRequestedRole());
        }

        roleChangeRequestRepository.save(request);
    }

    @Override
    public List<RoleChangeRequestEntity> getUserRoleRequests(UUID userId) {
        return roleChangeRequestRepository.findByUserIdOrderByRequestedAtDesc(userId);
    }

    @Override
    public void assignRole(User user, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // Check if user already has this role using the composite key
        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());
        if (!userRoleRepository.existsById(userRoleId)) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
        }
    }
}
