package org.learn.watchwave.auth.service.interfaces;

import org.learn.watchwave.auth.dto.request.UpdateUserRequest;
import org.learn.watchwave.auth.dto.request.ChangePasswordRequest;
import org.learn.watchwave.auth.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    // Existing methods
    User getCurrentUser(String token);

    User updateUser(UUID userId, UpdateUserRequest updateRequest);

    void changePassword(UUID userId, ChangePasswordRequest changePasswordRequest);

    Page<User> getAllUsers(Pageable pageable);

    User getUserById(UUID userId);

    void blockUser(UUID userId);

    void unblockUser(UUID userId);

    void deleteUser(UUID userId);

    Page<User> searchUsers(String query, Pageable pageable);

    User getPublicUserProfile(UUID userId);
}
