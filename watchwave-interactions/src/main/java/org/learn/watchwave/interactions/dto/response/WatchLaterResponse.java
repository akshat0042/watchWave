package org.learn.watchwave.interactions.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class WatchLaterResponse {
    private UUID id;
    private UUID userId;
    private UUID videoId;
    private Instant addedAt;
}
