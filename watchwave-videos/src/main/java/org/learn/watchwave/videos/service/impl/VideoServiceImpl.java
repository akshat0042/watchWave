package org.learn.watchwave.videos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.videos.dto.request.UploadVideoRequest;
import org.learn.watchwave.videos.dto.request.UpdateVideoRequest;
import org.learn.watchwave.videos.dto.response.VideoResponse;
import org.learn.watchwave.videos.dto.response.VideoListResponse;
import org.learn.watchwave.videos.enums.ProcessingStatus;
import org.learn.watchwave.videos.enums.VideoVisibility;
import org.learn.watchwave.videos.model.entity.Tag;
import org.learn.watchwave.videos.model.entity.Video;
import org.learn.watchwave.videos.model.entity.VideoTag;
import org.learn.watchwave.videos.repository.TagRepository;
import org.learn.watchwave.videos.repository.VideoRepository;
import org.learn.watchwave.videos.repository.VideoTagRepository;
import org.learn.watchwave.videos.service.VideoService;
import org.learn.watchwave.videos.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;
    private final VideoTagRepository videoTagRepository;
    private final UserRepository userRepository;
    private final AuthenticationHelper authHelper;

    @Value("${app.video.upload-dir}")
    private String videoUploadDir;

    @Value("${app.video.thumbnail-dir}")
    private String thumbnailUploadDir;

    @Value("${app.video.base-url}")
    private String baseUrl;

    @Override
    public VideoResponse uploadVideo(UploadVideoRequest request, Authentication authentication) {
        UUID uploaderId = authHelper.extractUserId(authentication);
        String username = authHelper.extractUsername(authentication);

        log.info("Starting video upload for user: {} ({})", username, uploaderId);

        // 1. Validate user exists
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new RuntimeException("User not found: " + uploaderId));

        // 2. Validate files
        validateVideoFile(request.getVideoFile());
        if (request.getThumbnailFile() != null) {
            validateThumbnailFile(request.getThumbnailFile());
        }

        // 3. Generate unique identifiers and filenames
        UUID videoId = UUID.randomUUID();
        String videoFileName = generateVideoFileName(videoId, request.getVideoFile());
        String thumbnailFileName = request.getThumbnailFile() != null ?
                generateThumbnailFileName(videoId, request.getThumbnailFile()) : null;

        // 4. Save files to disk
        String videoPath = saveVideoFile(request.getVideoFile(), videoFileName);
        String thumbnailPath = request.getThumbnailFile() != null ?
                saveThumbnailFile(request.getThumbnailFile(), thumbnailFileName) : null;

        // 5. Generate public URLs
        String videoUrl = generateVideoUrl(videoId);
        String thumbnailUrl = thumbnailPath != null ? generateThumbnailUrl(videoId) : null;

        // 6. Create video entity
        Video video = Video.builder()
                .id(videoId)
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .videoFilePath(videoPath)
                .thumbnailFilePath(thumbnailPath)
                .videoFileSize(request.getVideoFile().getSize())
                .thumbnailFileSize(request.getThumbnailFile() != null ?
                        request.getThumbnailFile().getSize() : null)
                .thumbnailContentType(request.getThumbnailFile() != null ?
                        request.getThumbnailFile().getContentType() : null)
                .uploader(uploader)
                .visibility(request.getVisibility())
                .isCommentEnabled(request.getIsCommentEnabled())
                .processingStatus(ProcessingStatus.UPLOADING)
                .views(0L)
                .isDeleted(false)
                .build();

        // 7. Save video to database
        Video savedVideo = videoRepository.save(video);
        log.info("Video saved with ID: {}", savedVideo.getId());

        // 8. Add tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            addTagsToVideo(savedVideo, request.getTags());
        }

        // 9. Update processing status to READY (for Phase 1)
        savedVideo.setProcessingStatus(ProcessingStatus.READY);
        videoRepository.save(savedVideo);

        log.info("Video upload completed successfully for ID: {}", savedVideo.getId());
        return convertToVideoResponse(savedVideo);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideoById(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

        if (video.getIsDeleted()) {
            throw new RuntimeException("Video has been deleted");
        }

        return convertToVideoResponse(video);
    }

    @Override
    public VideoResponse updateVideo(UUID videoId, UpdateVideoRequest request, Authentication authentication) {
        UUID currentUserId = authHelper.extractUserId(authentication);
        String userRole = authHelper.extractUserRole(authentication);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

        // Check ownership (admin can edit any video)
        if (!userRole.equals("ADMIN") && !video.getUploader().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: You can only update your own videos");
        }

        if (video.getIsDeleted()) {
            throw new RuntimeException("Cannot update deleted video");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            video.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            video.setDescription(request.getDescription());
        }
        if (request.getVisibility() != null) {
            video.setVisibility(request.getVisibility());
        }
        if (request.getIsCommentEnabled() != null) {
            video.setIsCommentEnabled(request.getIsCommentEnabled());
        }

        // Update thumbnail if provided
        if (request.getThumbnailFile() != null) {
            updateVideoThumbnail(video, request.getThumbnailFile());
        }

        // Update tags if provided
        if (request.getTags() != null) {
            updateVideoTags(video, request.getTags());
        }

        Video updatedVideo = videoRepository.save(video);
        log.info("Video updated successfully: {}", videoId);

        return convertToVideoResponse(updatedVideo);
    }

    @Override
    public void deleteVideo(UUID videoId, Authentication authentication) {
        UUID currentUserId = authHelper.extractUserId(authentication);
        String userRole = authHelper.extractUserRole(authentication);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

        // Check ownership (admin can delete any video)
        if (!userRole.equals("ADMIN") && !video.getUploader().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own videos");
        }

        // Soft delete
        video.setIsDeleted(true);
        videoRepository.save(video);

        if (userRole.equals("ADMIN")) {
            log.info("Video soft deleted by ADMIN: {} - Video ID: {}", currentUserId, videoId);
        } else {
            log.info("Video soft deleted by owner: {} - Video ID: {}", currentUserId, videoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VideoListResponse getAllPublicVideos(Pageable pageable) {
        Page<Video> videoPage = videoRepository.findByVisibilityAndIsDeletedFalseOrderByCreatedAtDesc(
                VideoVisibility.PUBLIC, pageable);

        Page<VideoResponse> responsePage = videoPage.map(this::convertToVideoResponse);
        return VideoListResponse.from(responsePage);
    }

    @Override
    public VideoListResponse getUserVideos(
            UUID userId,
            UUID currentUserId,
            String currentUserRole,
            Pageable pageable
    ) {
        boolean isOwnerOrAdmin = userId.equals(currentUserId) || "ADMIN".equals(currentUserRole);

        Page<Video> videoPage = isOwnerOrAdmin
                ? videoRepository.findByUploaderIdAndIsDeletedFalse(userId, pageable)
                : videoRepository.findPublicVideosByUserId(userId, pageable);

        return VideoListResponse.from(videoPage.map(this::convertToVideoResponse));
    }

    // VideoServiceImpl.java
    @Override
    @Transactional(readOnly = true)
    public VideoListResponse getCurrentUserVideos(Authentication authentication, Pageable pageable) {
        UUID currentUserId = authHelper.extractUserId(authentication);
        String currentUserRole = authHelper.extractUserRole(authentication);

        return getUserVideos(currentUserId, currentUserId, currentUserRole, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoListResponse getAllVideosForAdmin(Pageable pageable) {
        Page<Video> videoPage = videoRepository.findAll(pageable);
        Page<VideoResponse> responsePage = videoPage.map(this::convertToVideoResponse);
        return VideoListResponse.from(responsePage);
    }

    @Override
    public VideoResponse restoreVideo(UUID videoId, Authentication authentication) {
        UUID adminId = authHelper.extractUserId(authentication);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

        video.setIsDeleted(false);
        Video restoredVideo = videoRepository.save(video);

        log.info("Video restored by admin: {} - Video ID: {}", adminId, videoId);
        return convertToVideoResponse(restoredVideo);
    }

    @Override
    public void permanentlyDeleteVideo(UUID videoId, Authentication authentication) {
        UUID adminId = authHelper.extractUserId(authentication);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

        // Delete physical files
        try {
            if (video.getVideoFilePath() != null) {
                Files.deleteIfExists(Paths.get(video.getVideoFilePath()));
            }
            if (video.getThumbnailFilePath() != null) {
                Files.deleteIfExists(Paths.get(video.getThumbnailFilePath()));
            }
        } catch (IOException e) {
            log.error("Failed to delete physical files for video: {}", videoId, e);
        }

        // Delete from database
        videoTagRepository.deleteByVideoId(videoId);
        videoRepository.delete(video);

        log.info("Video permanently deleted by admin: {} - Video ID: {}", adminId, videoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID videoId) {
        return videoRepository.existsByIdAndIsDeletedFalse(videoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserVideos(UUID userId) {
        return videoRepository.countByUploaderIdAndIsDeletedFalse(userId);
    }

    // All your existing private helper methods remain the same...
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Video file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("File must be a video");
        }

        if (file.getSize() > 500 * 1024 * 1024) {
            throw new RuntimeException("Video file too large. Maximum size is 500MB");
        }
    }

    private void validateThumbnailFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Thumbnail must be an image");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Thumbnail file too large. Maximum size is 10MB");
        }
    }

    private String generateVideoFileName(UUID videoId, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".mp4";
        return videoId + "_" + System.currentTimeMillis() + extension;
    }

    private String generateThumbnailFileName(UUID videoId, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        return videoId + "_thumb_" + System.currentTimeMillis() + extension;
    }

    private String saveVideoFile(MultipartFile file, String fileName) {
        try {
            Path uploadPath = Paths.get(videoUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save video file: " + e.getMessage(), e);
        }
    }

    private String saveThumbnailFile(MultipartFile file, String fileName) {
        try {
            Path uploadPath = Paths.get(thumbnailUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save thumbnail file: " + e.getMessage(), e);
        }
    }

    private String generateVideoUrl(UUID videoId) {
        return baseUrl + "/api/videos/" + videoId + "/stream";
    }

    private String generateThumbnailUrl(UUID videoId) {
        return baseUrl + "/api/thumbnails/" + videoId;
    }

    private void addTagsToVideo(Video video, List<String> tagNames) {
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                Tag tag = findOrCreateTag(tagName.trim());
                VideoTag videoTag = new VideoTag(video, tag);
                videoTagRepository.save(videoTag);
            }
        }
    }

    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByNameIgnoreCase(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
    }

    private void updateVideoThumbnail(Video video, MultipartFile thumbnailFile) {
        validateThumbnailFile(thumbnailFile);

        String thumbnailFileName = generateThumbnailFileName(video.getId(), thumbnailFile);
        String thumbnailPath = saveThumbnailFile(thumbnailFile, thumbnailFileName);

        video.setThumbnailFilePath(thumbnailPath);
        video.setThumbnailFileSize(thumbnailFile.getSize());
        video.setThumbnailContentType(thumbnailFile.getContentType());
        video.setThumbnailUrl(generateThumbnailUrl(video.getId()));
    }

    private void updateVideoTags(Video video, List<String> tagNames) {
        videoTagRepository.deleteByVideoId(video.getId());
        addTagsToVideo(video, tagNames);
    }

    private VideoResponse convertToVideoResponse(Video video) {
        List<String> tagNames = videoTagRepository.findTagNamesByVideoId(video.getId());

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .uploaderId(video.getUploader().getId())
                .uploaderUsername(video.getUploader().getUsername())
                .views(video.getViews())
                .visibility(video.getVisibility())
                .processingStatus(video.getProcessingStatus())
                .isCommentEnabled(video.getIsCommentEnabled())
                .videoFileSize(video.getVideoFileSize())
                .videoDurationSeconds(video.getVideoDurationSeconds())
                .videoResolution(video.getVideoResolution())
                .tags(tagNames)
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }
}
