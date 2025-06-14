package org.learn.watchwave.interactions.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.interactions.dto.request.VideoLikeRequest;
import org.learn.watchwave.interactions.dto.response.VideoLikeResponse;
import org.learn.watchwave.interactions.dto.response.VideoLikeCount;
import org.learn.watchwave.interactions.service.VideoLikeService;
import org.learn.watchwave.videos.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/videos/{videoId}/likes")
@RequiredArgsConstructor
public class VideoLikeController {

    private final VideoLikeService videoLikeService;
    private final AuthenticationHelper authHelper;

    @PostMapping
    public ResponseEntity<VideoLikeResponse> likeOrDislikeVideo(
            @PathVariable UUID videoId,
            @RequestBody VideoLikeRequest request,
            Authentication authentication) {

        UUID userId = authHelper.extractUserId(authentication);

        // Ensure the videoId in the path matches the one in the request (optional, for safety)
        if (request.getVideoId() != null && !request.getVideoId().equals(videoId)) {
            return ResponseEntity.badRequest().build();
        }
        request.setVideoId(videoId);

        VideoLikeResponse response = videoLikeService.likeOrDislikeVideo(userId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping
    public ResponseEntity<Void> removeLikeOrDislike(@PathVariable UUID videoId, Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        videoLikeService.removeLikeOrDislike(userId, videoId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/count")
    public ResponseEntity<VideoLikeCount> getLikeDislikeCount(@PathVariable UUID videoId) {
        VideoLikeCount count = videoLikeService.getLikeDislikeCount(videoId);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/me")
    public ResponseEntity<Boolean> getUserLikeStatus(
            @PathVariable UUID videoId,
            Authentication authentication) {

        UUID userId = authHelper.extractUserId(authentication);
        Boolean liked = videoLikeService.getUserLikeStatus(userId, videoId);
        return ResponseEntity.ok(liked);
    }
}
