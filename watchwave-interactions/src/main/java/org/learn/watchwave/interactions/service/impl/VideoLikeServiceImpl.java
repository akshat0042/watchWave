package org.learn.watchwave.interactions.service.impl;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.videos.service.VideoService;
import org.learn.watchwave.interactions.dto.request.VideoLikeRequest;
import org.learn.watchwave.interactions.dto.response.VideoLikeResponse;
import org.learn.watchwave.interactions.dto.response.VideoLikeCount;
import org.learn.watchwave.interactions.model.entity.VideoLike;
import org.learn.watchwave.interactions.repository.VideoLikeRepository;
import org.learn.watchwave.interactions.service.VideoLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoLikeServiceImpl implements VideoLikeService {

    private final VideoLikeRepository videoLikeRepository;
    private final UserRepository userRepository;
    private final VideoService videoService;

    @Override
    @Transactional
    public VideoLikeResponse likeOrDislikeVideo(UUID userId, VideoLikeRequest request) {
        // Validate user exists (no entity modification)
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // Validate video exists (no entity modification)
        videoService.getVideoById(request.getVideoId());

        // Find existing like/dislike
        Optional<VideoLike> existing = videoLikeRepository.findByUserIdAndVideoId(userId, request.getVideoId());
        VideoLike videoLike;
        if (existing.isPresent()) {
            videoLike = existing.get();
            videoLike.setLiked(request.getLiked());
        } else {
            videoLike = VideoLike.builder()
                    .userId(userId)
                    .videoId(request.getVideoId())
                    .liked(request.getLiked())
                    .build();
        }
        VideoLike saved = videoLikeRepository.save(videoLike);

        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void removeLikeOrDislike(UUID userId, UUID videoId) {
        // Validate user and video
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        videoService.getVideoById(videoId);

        videoLikeRepository.deleteByUserIdAndVideoId(userId, videoId);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoLikeCount getLikeDislikeCount(UUID videoId) {
        // Validate video exists
        videoService.getVideoById(videoId);

        long likes = videoLikeRepository.countByVideoIdAndLiked(videoId, true);
        long dislikes = videoLikeRepository.countByVideoIdAndLiked(videoId, false);
        return new VideoLikeCount(likes, dislikes);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean getUserLikeStatus(UUID userId, UUID videoId) {
        return videoLikeRepository.findByUserIdAndVideoId(userId, videoId)
                .map(VideoLike::getLiked)
                .orElse(null);
    }

    // Helper to map entity to response DTO
    private VideoLikeResponse toResponseDTO(VideoLike like) {
        VideoLikeResponse dto = new VideoLikeResponse();
        dto.setId(like.getId());
        dto.setUserId(like.getUserId());
        dto.setVideoId(like.getVideoId());
        dto.setLiked(like.getLiked());
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }
}
