package org.learn.watchwave.interactions.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class VideoLikeRequest {
    private UUID videoId;
    private Boolean liked;
}
