package org.learn.watchwave.auth.controller;

import jakarta.validation.Valid;
import org.learn.watchwave.auth.dto.request.ChangePasswordRequest;
import org.learn.watchwave.auth.dto.request.UpdateUserRequest;
import org.learn.watchwave.auth.dto.response.UserResponseDTO;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.service.interfaces.TokenService;
import org.learn.watchwave.auth.service.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }


    //change this
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        User user = userService.getCurrentUser(token);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setProfilePicUrl(user.getProfilePicUrl());
        response.setBlocked(user.isBlocked());
        response.setVerified(user.isVerified());

        // Get roles safely without lazy loading issues
        List<String> roleNames = user.getRoles().stream()
                .map(userRole -> {
                    try {
                        return userRole.getRole().getRoleName();
                    } catch (Exception e) {
                        return "USER";
                    }
                })
                .collect(Collectors.toList());
        response.setRoles(roleNames);

        if (user.getUserProfile() != null) {
            response.setGender(user.getUserProfile().getGender());
            response.setBirthdate(user.getUserProfile().getBirthdate());
            response.setLocation(user.getUserProfile().getLocation());
            response.setBio(user.getUserProfile().getBio());
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateUserRequest updateRequest) {

        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        User updatedUser = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/me/password")
    @PreAuthorize("hasRole('USER') or hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok("Password changed successfully");
    }

    // Public endpoint for getting user profile by ID (for viewing other users)
    @GetMapping("/{userId}/profile")
    public ResponseEntity<User> getUserProfile(@PathVariable UUID userId) {
        User user = userService.getPublicUserProfile(userId);
        return ResponseEntity.ok(user);
    }
}
