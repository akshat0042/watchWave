package org.learn.watchwave.auth.service.interfaces;

import org.learn.watchwave.auth.dto.request.UpdateProfileRequest;
import org.learn.watchwave.auth.model.entity.UserProfile;

import java.util.UUID;

public interface ProfileService {

    UserProfile updateProfile(UUID userId, UpdateProfileRequest updateRequest);

    UserProfile getPublicProfile(UUID userId); // Add this method
}
