package org.learn.watchwave.videos.model.entity;

import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.videos.enums.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "videos", schema = "videos")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Video {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Video file information
    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "video_file_path")
    private String videoFilePath;

    @Column(name = "video_file_size")
    private Long videoFileSize;

    @Column(name = "video_duration_seconds")
    private Integer videoDurationSeconds;

    @Column(name = "video_resolution", length = 20)
    private String videoResolution;

    // Thumbnail information
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "thumbnail_file_path")
    private String thumbnailFilePath;

    @Column(name = "thumbnail_file_size")
    private Long thumbnailFileSize;

    @Column(name = "thumbnail_content_type", length = 50)
    private String thumbnailContentType;

    // Video metadata
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    @JsonIgnore
    private User uploader;

    @Column(name = "views")
    private Long views = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 10)
    private VideoVisibility visibility = VideoVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", length = 20)
    private ProcessingStatus processingStatus = ProcessingStatus.UPLOADING;

    // Settings
    @Column(name = "is_comment_enabled")
    private Boolean isCommentEnabled = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Timestamps
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Video-Tag relationship
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VideoTag> videoTags = new HashSet<>();

    // Constructor
    public Video() {
        this.id = UUID.randomUUID();
        this.createdAt = Timestamp.from(Instant.now());
        this.updatedAt = Timestamp.from(Instant.now());
        this.videoTags = new HashSet<>();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }
}
