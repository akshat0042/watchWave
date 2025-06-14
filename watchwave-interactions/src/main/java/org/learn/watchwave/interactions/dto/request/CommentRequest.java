package org.learn.watchwave.interactions.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CommentRequest {
    private UUID parentId; // null for top-level comments
    private String content;
}