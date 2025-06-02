package org.learn.watchwave.videos.dto.response;

import lombok.*;
import org.learn.watchwave.videos.enums.VideoVisibility;
import org.learn.watchwave.videos.enums.ProcessingStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponse {

    private UUID id;
    private String title;
    private String description;

    private String videoUrl;
    private String thumbnailUrl;

    private UUID uploaderId;
    private String uploaderUsername;

    private Long views;
    private VideoVisibility visibility;
    private ProcessingStatus processingStatus;
    private Boolean isCommentEnabled;

    private Long videoFileSize;
    private Integer videoDurationSeconds;
    private String videoResolution;

    private List<String> tags;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
