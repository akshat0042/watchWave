package org.learn.watchwave.auth.controller;

import jakarta.validation.Valid;
import org.learn.watchwave.auth.dto.request.UpdateProfileRequest;
import org.learn.watchwave.auth.model.entity.UserProfile;
import org.learn.watchwave.auth.service.interfaces.ProfileService;
import org.learn.watchwave.auth.service.interfaces.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final TokenService tokenService;

    public ProfileController(ProfileService profileService, TokenService tokenService) {
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest updateRequest) {

        UUID userId = tokenService.extractUserIdFromToken(authHeader);
        UserProfile updatedProfile = profileService.updateProfile(userId, updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable UUID userId) {
        UserProfile profile = profileService.getPublicProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
