package org.learn.watchwave.videos.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.videos.dto.request.UploadVideoRequest;
import org.learn.watchwave.videos.dto.request.UpdateVideoRequest;
import org.learn.watchwave.videos.dto.response.VideoResponse;
import org.learn.watchwave.videos.dto.response.VideoListResponse;
import org.learn.watchwave.videos.service.VideoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<VideoResponse> uploadVideo(
            @Valid @ModelAttribute UploadVideoRequest request,
            Authentication authentication) {

        VideoResponse response = videoService.uploadVideo(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable UUID videoId) {
        VideoResponse response = videoService.getVideoById(videoId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<VideoResponse> updateVideo(
            @PathVariable UUID videoId,
            @Valid @ModelAttribute UpdateVideoRequest request,
            Authentication authentication) {

        VideoResponse response = videoService.updateVideo(videoId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable UUID videoId,
            Authentication authentication) {

        videoService.deleteVideo(videoId, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<VideoListResponse> getAllPublicVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        VideoListResponse response = videoService.getAllPublicVideos(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<VideoListResponse> getUserVideos(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        VideoListResponse response = videoService.getUserVideos(userId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoListResponse> getCurrentUserVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);
        VideoListResponse response = videoService.getCurrentUserVideos(authentication, pageable);

        return ResponseEntity.ok(response);
    }
}
