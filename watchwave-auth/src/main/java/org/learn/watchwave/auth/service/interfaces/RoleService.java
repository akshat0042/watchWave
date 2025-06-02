package org.learn.watchwave.auth.service.interfaces;

import org.learn.watchwave.auth.model.entity.RoleChangeRequestEntity;
import org.learn.watchwave.auth.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    void requestRoleChange(UUID userId, String requestedRole);

    List<RoleChangeRequestEntity> getPendingRoleRequests();

    void processRoleRequest(UUID requestId, String status);

    List<RoleChangeRequestEntity> getUserRoleRequests(UUID userId);

    void assignRole(User user, String roleName);

    void upgradeToCreator(UUID userId);
}
