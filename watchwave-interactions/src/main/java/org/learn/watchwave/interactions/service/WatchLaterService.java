package org.learn.watchwave.interactions.service;

import org.learn.watchwave.interactions.dto.request.WatchLaterRequest;
import org.learn.watchwave.interactions.dto.response.WatchLaterResponse;
import java.util.List;
import java.util.UUID;

public interface WatchLaterService {
    WatchLaterResponse addToWatchLater(UUID userId, WatchLaterRequest request);
    void removeFromWatchLater(UUID userId, UUID videoId);
    List<WatchLaterResponse> getWatchLaterList(UUID userId);
    boolean isInWatchLater(UUID userId, UUID videoId);
}
