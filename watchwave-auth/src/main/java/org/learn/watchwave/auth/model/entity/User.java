package org.learn.watchwave.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="users", schema="auth")
@Getter
@Setter
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore  // ADD THIS - hides password in JSON responses
    private String passwordHash;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at")
    private Timestamp updatedAt = Timestamp.from(Instant.now());

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();
}