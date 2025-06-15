package org.learn.watchwave.interactions.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "watch_later", schema = "interactions",uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class WatchLater {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "added_at")
    private Instant addedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (addedAt == null) addedAt = Instant.now();
    }
}
