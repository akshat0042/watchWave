package org.learn.watchwave.interactions.service;

import org.learn.watchwave.interactions.dto.request.VideoLikeRequest;
import org.learn.watchwave.interactions.dto.response.VideoLikeResponse;
import org.learn.watchwave.interactions.dto.response.VideoLikeCount;

import java.util.UUID;

public interface VideoLikeService {
    VideoLikeResponse likeOrDislikeVideo(UUID userId, VideoLikeRequest request);
    void removeLikeOrDislike(UUID userId, UUID videoId);
    VideoLikeCount getLikeDislikeCount(UUID videoId);
    Boolean getUserLikeStatus(UUID userId, UUID videoId); // true=like, false=dislike, null=none
}
