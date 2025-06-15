package org.learn.watchwave.interactions.repository;

import org.learn.watchwave.interactions.model.entity.WatchLater;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WatchLaterRepository extends JpaRepository<WatchLater, UUID> {
    Optional<WatchLater> findByUserIdAndVideoId(UUID userId, UUID videoId);
    List<WatchLater> findByUserIdOrderByAddedAtDesc(UUID userId);
    void deleteByUserIdAndVideoId(UUID userId, UUID videoId);
}
