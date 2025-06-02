package org.learn.watchwave.videos.model.entity;

import lombok.*;
import jakarta.persistence.*;
import org.learn.watchwave.videos.model.id.VideoTagId;

@Entity
@Table(name = "video_tags", schema = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoTag {

    @EmbeddedId  // ← Changed from @Id
    private VideoTagId id;  // ← Uses composite key, not UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    @MapsId("videoId")  // ← Maps to composite key
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    @MapsId("tagId")    // ← Maps to composite key
    private Tag tag;

    // Convenience constructor
    public VideoTag(Video video, Tag tag) {
        this.video = video;
        this.tag = tag;
        this.id = new VideoTagId(video.getId(), tag.getId());
    }
}
