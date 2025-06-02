package org.learn.watchwave.videos.repository;

import org.learn.watchwave.videos.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    // Find tag by name (case-insensitive) - for finding existing tags
    Optional<Tag> findByNameIgnoreCase(String name);

    // Check if tag exists by name (case-insensitive) - for validation
    boolean existsByNameIgnoreCase(String name);
}
