package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.dto.request.ChangePasswordRequest;
import org.learn.watchwave.auth.dto.request.UpdateUserRequest;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.service.interfaces.UserService;
import org.learn.watchwave.auth.service.jwt.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(String token) {
        UUID userId = jwtService.extractUserId(token);
        return userRepository.findByIdWithRoles(userId)  // ← Change this line
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(UUID userId, UpdateUserRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store the original password hash
        String originalPasswordHash = user.getPasswordHash();

        if (updateRequest.getUsername() != null) {
            if (userRepository.existsByUsername(updateRequest.getUsername()) &&
                    !user.getUsername().equals(updateRequest.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null) {
            if (userRepository.existsByEmail(updateRequest.getEmail()) &&
                    !user.getEmail().equals(updateRequest.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(updateRequest.getEmail());
            user.setVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
        }

        if (updateRequest.getProfilePicUrl() != null) {
            user.setProfilePicUrl(updateRequest.getProfilePicUrl());
        }

        // ENSURE PASSWORD HASH IS NEVER NULL
        user.setPasswordHash(originalPasswordHash);
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        User updatedUser = userRepository.save(user);

        // Create a safe copy for response (don't modify the managed entity)
        User responseUser = new User();
        responseUser.setId(updatedUser.getId());
        responseUser.setUsername(updatedUser.getUsername());
        responseUser.setEmail(updatedUser.getEmail());
        responseUser.setProfilePicUrl(updatedUser.getProfilePicUrl());
        responseUser.setBlocked(updatedUser.isBlocked());
        responseUser.setVerified(updatedUser.isVerified());
        responseUser.setCreatedAt(updatedUser.getCreatedAt());
        responseUser.setUpdatedAt(updatedUser.getUpdatedAt());
        responseUser.setUserProfile(updatedUser.getUserProfile());

        return responseUser;
    }

    @Override
    public void changePassword(UUID userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepository.save(user);
    }

    // Admin methods implementation
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        // Remove passwords for security
        users.forEach(user -> user.setPasswordHash(null));
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(null); // Remove password for security
        return user;
    }

    @Override
    public void blockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(true);
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepository.save(user);
    }

    @Override
    public void unblockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(false);
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Soft delete - mark as deleted instead of actually deleting
        // This preserves referential integrity for videos, comments, etc.
        user.setBlocked(true);
        user.setEmail("deleted_" + userId + "@deleted.com");
        user.setUsername("deleted_" + userId);
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepository.save(user);

        // Alternative: Hard delete (use with caution)
        // userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String query, Pageable pageable) {
        Page<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable);
        // Remove passwords for security
        users.forEach(user -> user.setPasswordHash(null));
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User getPublicUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return only public information
        user.setPasswordHash(null);
        user.setVerificationToken(null);
        return user;
    }
}
