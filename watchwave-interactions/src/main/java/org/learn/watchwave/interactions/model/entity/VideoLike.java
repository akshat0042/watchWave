package org.learn.watchwave.interactions.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_likes", schema = "interactions",uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class VideoLike {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "is_like", nullable = false)
    private Boolean liked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
