package org.learn.watchwave.interactions.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
public class VideoLikeResponse {
    private UUID id;
    private UUID userId;
    private UUID videoId;
    private Boolean liked;
    private Instant createdAt;
}
