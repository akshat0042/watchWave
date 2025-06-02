package org.learn.watchwave.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleChangeRequest {
    @NotBlank(message = "Requested role is required")
    @Pattern(regexp = "USER|CREATOR|ADMIN", message = "Role must be USER, CREATOR, or ADMIN")
    private String requestedRole;
}
