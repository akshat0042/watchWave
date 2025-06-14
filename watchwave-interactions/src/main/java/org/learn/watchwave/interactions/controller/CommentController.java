package org.learn.watchwave.interactions.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.interactions.dto.request.CommentRequest;
import org.learn.watchwave.interactions.dto.response.CommentResponse;
import org.learn.watchwave.interactions.service.CommentService;
import org.learn.watchwave.videos.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final AuthenticationHelper authHelper;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID videoId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        CommentResponse response = commentService.addComment(userId, videoId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID videoId,
            @PathVariable UUID commentId,
            Authentication authentication) {
        UUID userId = authHelper.extractUserId(authentication);
        List<String> roles = authHelper.extractUserRoles(authentication);
        commentService.deleteComment(userId, roles, commentId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID videoId) {
        return ResponseEntity.ok(commentService.getCommentsForVideo(videoId));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }
}
