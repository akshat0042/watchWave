package org.learn.watchwave.videos.service;

import org.learn.watchwave.videos.dto.request.UploadVideoRequest;
import org.learn.watchwave.videos.dto.request.UpdateVideoRequest;
import org.learn.watchwave.videos.dto.response.VideoResponse;
import org.learn.watchwave.videos.dto.response.VideoListResponse;
import org.springframework.data.domain.Pageable;
import org.learn.watchwave.videos.model.entity.Video;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface VideoService {

    VideoResponse uploadVideo(UploadVideoRequest request, Authentication authentication);
    VideoResponse getVideoById(UUID videoId);
    VideoResponse updateVideo(UUID videoId, UpdateVideoRequest request, Authentication authentication);
    void deleteVideo(UUID videoId, Authentication authentication);

    VideoListResponse getAllPublicVideos(Pageable pageable);
    VideoListResponse getUserVideos(
            UUID userId,
            UUID currentUserId,
            String currentUserRole,
            Pageable pageable
    );
    VideoListResponse getCurrentUserVideos(Authentication authentication, Pageable pageable);

    VideoListResponse getAllVideosForAdmin(Pageable pageable);
    VideoResponse restoreVideo(UUID videoId, Authentication authentication);
    void permanentlyDeleteVideo(UUID videoId, Authentication authentication);
    Video getVideoEntityById(UUID videoId);
    boolean existsById(UUID videoId);
    long countUserVideos(UUID userId);
}
