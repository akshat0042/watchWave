package org.learn.watchwave.interactions.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.interactions.dto.request.WatchLaterRequest;
import org.learn.watchwave.interactions.dto.response.WatchLaterResponse;
import org.learn.watchwave.interactions.service.WatchLaterService;
import org.learn.watchwave.videos.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/watch-later")
@RequiredArgsConstructor
public class WatchLaterController {

    private final WatchLaterService watchLaterService;
    private final AuthenticationHelper authHelper;

    @PostMapping
    public ResponseEntity<WatchLaterResponse> addToWatchLater(
            @RequestBody WatchLaterRequest request,
            Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        WatchLaterResponse response = watchLaterService.addToWatchLater(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> removeFromWatchLater(
            @PathVariable UUID videoId,
            Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        watchLaterService.removeFromWatchLater(userId, videoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WatchLaterResponse>> getWatchLaterList(Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        return ResponseEntity.ok(watchLaterService.getWatchLaterList(userId));
    }

    @GetMapping("/contains/{videoId}")
    public ResponseEntity<Boolean> isInWatchLater(
            @PathVariable UUID videoId,
            Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        return ResponseEntity.ok(watchLaterService.isInWatchLater(userId, videoId));
    }
}
