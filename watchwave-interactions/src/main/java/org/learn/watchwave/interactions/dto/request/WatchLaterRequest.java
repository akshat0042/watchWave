package org.learn.watchwave.interactions.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class WatchLaterRequest {
    private UUID videoId;
}
