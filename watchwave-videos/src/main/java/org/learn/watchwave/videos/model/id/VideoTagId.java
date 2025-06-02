package org.learn.watchwave.videos.model.id;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTagId implements Serializable {

    @Column(name = "video_id")
    private UUID videoId;

    @Column(name = "tag_id")
    private UUID tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoTagId that = (VideoTagId) o;
        return Objects.equals(videoId, that.videoId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, tagId);
    }
}
