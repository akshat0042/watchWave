//package org.learn.watchwave.auth.model.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//
//import java.util.UUID;
//
//@Entity
//@Table(name = "role_change_requests", schema = "auth")
//@Getter
//@Setter
//public class RoleChangeRequest {
//
//    @Id
//    private UUID id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Column(name = "requested_role")
//    private String requestedRole;
//
//    private String status; // "PENDING", "APPROVED", "REJECTED"
//
//    @Column(name = "requested_at")
//    private Timestamp requestedAt = Timestamp.from(Instant.now());
//
//    @Column(name = "reviewed_at")
//    private Timestamp reviewedAt;
//}
