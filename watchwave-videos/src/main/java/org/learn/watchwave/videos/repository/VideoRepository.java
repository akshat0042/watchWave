package org.learn.watchwave.videos.repository;

import org.learn.watchwave.videos.model.entity.Video;
import org.learn.watchwave.videos.enums.VideoVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    // Get user's videos with pagination
    Page<Video> findByUploaderIdAndIsDeletedFalse(UUID uploaderId, Pageable pageable);

    // Get public videos (for homepage)
    Page<Video> findByVisibilityAndIsDeletedFalseOrderByCreatedAtDesc(
            VideoVisibility visibility, Pageable pageable);

    // Count user's videos
    long countByUploaderIdAndIsDeletedFalse(UUID uploaderId);

    // Check if video exists
    boolean existsByIdAndIsDeletedFalse(UUID id);

    // VideoRepository.java
    @Query("SELECT v FROM Video v WHERE v.uploader.id = :userId " +
            "AND v.visibility = 'PUBLIC' " +
            "AND v.isDeleted = false")
    Page<Video> findPublicVideosByUserId(@Param("userId") UUID userId, Pageable pageable);

}
