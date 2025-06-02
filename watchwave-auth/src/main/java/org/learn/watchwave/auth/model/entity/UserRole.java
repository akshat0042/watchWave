package org.learn.watchwave.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.learn.watchwave.auth.model.id.UserRoleId;

@Entity
@Table(name = "user_roles", schema = "auth")
@Getter
@Setter
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @JsonIgnore  // This is correct
    private Role role;

    // Add this method to safely get role name for JSON
    @JsonProperty("roleName")
    public String getRoleName() {
        try {
            return role != null ? role.getRoleName() : null;
        } catch (Exception e) {
            return "USER"; // fallback
        }
    }

    // Constructors
    public UserRole() {
        this.id = new UserRoleId();
    }

    public UserRole(User user, Role role) {
        this.id = new UserRoleId(user.getId(), role.getId());
        this.user = user;
        this.role = role;
    }

    // Convenience methods
    public void setUser(User user) {
        this.user = user;
        if (this.id == null) {
            this.id = new UserRoleId();
        }
        this.id.setUserId(user.getId());
    }

    public void setRole(Role role) {
        this.role = role;
        if (this.id == null) {
            this.id = new UserRoleId();
        }
        this.id.setRoleId(role.getId());
    }
}
