package org.learn.watchwave.interactions.repository;

import org.learn.watchwave.interactions.model.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, UUID> {
    Optional<VideoLike> findByUserIdAndVideoId(UUID userId, UUID videoId);
    long countByVideoIdAndLiked(UUID videoId, boolean liked);
    void deleteByUserIdAndVideoId(UUID userId, UUID videoId);
}
