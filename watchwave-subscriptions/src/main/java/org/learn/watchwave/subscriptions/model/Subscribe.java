package org.learn.watchwave.subscriptions.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subscriptions", schema = "subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class Subscribe {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "subscriber_id", nullable = false)
    private UUID subscriberId;

    @Column(name = "creator_id",nullable = false)
    private UUID creatorId;

    @Column(name = "subscribed_at")
    private Instant subscribedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (subscribedAt == null) subscribedAt = Instant.now();
    }
}
