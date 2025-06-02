package org.learn.watchwave.videos.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.videos.dto.response.VideoResponse;
import org.learn.watchwave.videos.dto.response.VideoListResponse;
import org.learn.watchwave.videos.service.VideoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/videos/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Class-level security
public class AdminVideoController {

    private final VideoService videoService;

    @GetMapping("/all")
    public ResponseEntity<VideoListResponse> getAllVideosForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        VideoListResponse response = videoService.getAllVideosForAdmin(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{videoId}/restore")
    public ResponseEntity<VideoResponse> restoreVideo(
            @PathVariable UUID videoId,
            Authentication authentication) {

        VideoResponse response = videoService.restoreVideo(videoId, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{videoId}/permanent")
    public ResponseEntity<Void> permanentlyDeleteVideo(
            @PathVariable UUID videoId,
            Authentication authentication) {

        videoService.permanentlyDeleteVideo(videoId, authentication);
        return ResponseEntity.noContent().build();
    }
}
