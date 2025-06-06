package org.learn.watchwave.videos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.learn.watchwave.videos.service.StreamingService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StreamingController {

    private final StreamingService streamingService;

    @GetMapping("/videos/{videoId}/stream")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable UUID videoId,
            @RequestHeader(value = "Range", required = false) String range) {

        log.info("Streaming video: {} with range: {}", videoId, range);
        return streamingService.streamVideo(videoId, range);
    }

    @GetMapping("/thumbnails/{videoId}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable UUID videoId) {
        log.info("Serving thumbnail for video: {}", videoId);
        return streamingService.getThumbnail(videoId);
    }
}
