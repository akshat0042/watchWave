package org.learn.watchwave.videos.service;

import org.learn.watchwave.videos.dto.request.UploadVideoRequest;
import org.learn.watchwave.videos.dto.request.UpdateVideoRequest;
import org.learn.watchwave.videos.dto.response.VideoResponse;
import org.learn.watchwave.videos.dto.response.VideoListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface VideoService {

    // Core CRUD operations - Updated to use Authentication
    VideoResponse uploadVideo(UploadVideoRequest request, Authentication authentication);
    VideoResponse getVideoById(UUID videoId);
    VideoResponse updateVideo(UUID videoId, UpdateVideoRequest request, Authentication authentication);
    void deleteVideo(UUID videoId, Authentication authentication);

    // Video listing operations
    VideoListResponse getAllPublicVideos(Pageable pageable);
    VideoListResponse getUserVideos(
            UUID userId,
            UUID currentUserId,
            String currentUserRole,
            Pageable pageable
    );
    VideoListResponse getCurrentUserVideos(Authentication authentication, Pageable pageable);

    // Admin operations - Updated to use Authentication
    VideoListResponse getAllVideosForAdmin(Pageable pageable);
    VideoResponse restoreVideo(UUID videoId, Authentication authentication);
    void permanentlyDeleteVideo(UUID videoId, Authentication authentication);

    // Utility operations
    boolean existsById(UUID videoId);
    long countUserVideos(UUID userId);
}
