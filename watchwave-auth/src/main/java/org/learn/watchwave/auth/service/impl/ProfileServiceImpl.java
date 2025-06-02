package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.dto.request.UpdateProfileRequest;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.model.entity.UserProfile;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.service.interfaces.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getPublicProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getUserProfile();
    }

    @Override
    public UserProfile updateProfile(UUID userId, UpdateProfileRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        if (updateRequest.getGender() != null) {
            profile.setGender(updateRequest.getGender());
        }

        if (updateRequest.getBirthdate() != null) {
            profile.setBirthdate(updateRequest.getBirthdate());
        }

        if (updateRequest.getLocation() != null) {
            profile.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getBio() != null) {
            profile.setBio(updateRequest.getBio());
        }

        user.setUserProfile(profile);
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        User updatedUser = userRepository.save(user);
        return updatedUser.getUserProfile();
    }
}
