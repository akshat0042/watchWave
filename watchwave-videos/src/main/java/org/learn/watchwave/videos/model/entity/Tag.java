package org.learn.watchwave.videos.model.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tags", schema = "videos")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    // Constructor
    public Tag() {
        this.id = UUID.randomUUID();
    }

    // Constructor with name
    public Tag(String name) {
        this();
        this.name = name.toLowerCase().trim();
    }
}
