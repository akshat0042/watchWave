package org.learn.watchwave.interactions.service.impl;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.interactions.dto.request.CommentRequest;
import org.learn.watchwave.interactions.dto.response.CommentResponse;
import org.learn.watchwave.interactions.model.entity.Comments;
import org.learn.watchwave.interactions.repository.CommentsRepository;
import org.learn.watchwave.interactions.service.CommentService;
import org.learn.watchwave.videos.model.entity.Video;
import org.learn.watchwave.videos.service.VideoService;
import org.learn.watchwave.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoService videoService;

    @Override
    @Transactional
    public CommentResponse addComment(UUID userId, UUID videoId, CommentRequest request) {
        // Validate user and video
        if (!userRepository.existsById(userId)) throw new IllegalArgumentException("User not found");
        videoService.getVideoById(videoId);

        Comments comment = Comments.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .videoId(videoId)
                .parentId(request.getParentId())
                .content(request.getContent())
                .createdAt(Instant.now())
                .deleted(false)
                .build();

        Comments saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    /**
     * Role-based comment deletion:
     * - Comment author can delete their own comment
     * - Video owner can delete any comment on their video
     * - Admin can delete any comment
     */
    @Override
    @Transactional
    public void deleteComment(UUID userId, List<String> roles, UUID commentId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Allow comment author
        if (comment.getUserId().equals(userId)) {
            // allowed
        }
        // Allow video owner
        else {
            Video video = videoService.getVideoEntityById(comment.getVideoId());
            if (video.getUploader().getId().equals(userId)) {
                // allowed
            }
            // Allow admin
            else if (roles != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"))) {
                // allowed
            }
            else {
                throw new SecurityException("You do not have permission to delete this comment.");
            }
        }

        comment.setDeleted(true);
        comment.setDeletedAt(Instant.now());
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsForVideo(UUID videoId) {
        return commentRepository.findByVideoIdAndDeletedFalseOrderByCreatedAtAsc(videoId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(UUID parentId) {
        return commentRepository.findByParentIdAndDeletedFalseOrderByCreatedAtAsc(parentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CommentResponse toResponse(Comments comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setVideoId(comment.getVideoId());
        dto.setParentId(comment.getParentId());
        dto.setContent(comment.getContent());
        dto.setDeleted(comment.isDeleted());
        dto.setDeletedAt(comment.getDeletedAt());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
