package org.learn.watchwave.interactions.dto.response;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private UUID userId;
    private UUID videoId;
    private UUID parentId;
    private String content;
    private boolean Deleted;
    private Instant deletedAt;
    private Instant createdAt;
}