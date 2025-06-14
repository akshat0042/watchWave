package org.learn.watchwave.interactions.service;

import org.learn.watchwave.interactions.dto.request.CommentRequest;
import org.learn.watchwave.interactions.dto.response.CommentResponse;
import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse addComment(UUID userId, UUID videoId, CommentRequest request);
    void deleteComment(UUID userId, List<String> roles, UUID commentId);
    List<CommentResponse> getCommentsForVideo(UUID videoId);
    List<CommentResponse> getReplies(UUID parentId);
}