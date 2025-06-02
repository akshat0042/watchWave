package org.learn.watchwave.auth.repository;

import java.util.UUID;

import org.learn.watchwave.auth.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}