package org.learn.watchwave.videos.repository;

import org.learn.watchwave.videos.model.entity.VideoTag;
import org.learn.watchwave.videos.model.id.VideoTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoTagRepository extends JpaRepository<VideoTag, VideoTagId> {

    // Find all video-tag relationships for a specific video returns a list
    List<VideoTag> findByVideoId(UUID videoId);

    // Find all video-tag relationships for a specific tag
    List<VideoTag> findByTagId(UUID tagId);

    // Delete all tags for a video (used in updateVideoTags)
    @Modifying
    @Query("DELETE FROM VideoTag vt WHERE vt.video.id = :videoId")
    void deleteByVideoId(@Param("videoId") UUID videoId);

    // Check if a video has a specific tag
    boolean existsByVideoIdAndTagId(UUID videoId, UUID tagId);

    // Get tag names for a video (used in convertToVideoResponse) returns string
    @Query("SELECT vt.tag.name FROM VideoTag vt WHERE vt.video.id = :videoId")
    List<String> findTagNamesByVideoId(@Param("videoId") UUID videoId);

    // Count how many videos use a specific tag
    long countByTagId(UUID tagId);

    // Find videos by tag name (for future search functionality)
    @Query("SELECT vt.video.id FROM VideoTag vt WHERE vt.tag.name = :tagName")
    List<UUID> findVideoIdsByTagName(@Param("tagName") String tagName);
}
