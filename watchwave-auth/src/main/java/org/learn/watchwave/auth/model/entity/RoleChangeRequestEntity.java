package org.learn.watchwave.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;  // ADD THIS IMPORT
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "role_change_requests", schema = "auth")
@Getter
@Setter
public class RoleChangeRequestEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "requested_role", nullable = false, length = 50)
    private String requestedRole;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @Column(name = "reviewed_at")
    private Timestamp reviewedAt;

    // Constructor
    public RoleChangeRequestEntity() {
        this.id = UUID.randomUUID();
        this.requestedAt = Timestamp.from(Instant.now());
    }

    // Fix the relationship - add @JsonIgnore to prevent serialization issues
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore  // ADD THIS
    private User user;

    // Add safe methods to get user details for JSON
    @JsonProperty("username")  // This needs the import above
    public String getUsername() {
        try {
            return user != null ? user.getUsername() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @JsonProperty("userEmail")  // This needs the import above
    public String getUserEmail() {
        try {
            return user != null ? user.getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
