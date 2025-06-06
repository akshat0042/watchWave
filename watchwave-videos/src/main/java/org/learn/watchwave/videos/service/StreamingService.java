package org.learn.watchwave.videos.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.util.UUID;

public interface StreamingService {
    ResponseEntity<Resource> streamVideo(UUID videoId, String range);
    ResponseEntity<Resource> getThumbnail(UUID videoId);
}
