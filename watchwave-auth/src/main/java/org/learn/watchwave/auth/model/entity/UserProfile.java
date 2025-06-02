package org.learn.watchwave.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.sql.Timestamp;

@Entity
@Table(name = "user_profiles", schema = "auth")
@Getter
@Setter
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonIgnore  // ADD THIS
    private User user;

    @Column(length = 10)
    private String gender;

    private LocalDate birthdate;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
