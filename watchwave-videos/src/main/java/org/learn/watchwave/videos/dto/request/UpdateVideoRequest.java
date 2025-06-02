package org.learn.watchwave.videos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.learn.watchwave.videos.enums.VideoVisibility;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVideoRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private VideoVisibility visibility;

    private Boolean isCommentEnabled;

    // Optional: Update thumbnail
    private MultipartFile thumbnailFile;

    // Tags (replaces existing tags)
    private List<@NotBlank @Size(max = 50) String> tags;
}
