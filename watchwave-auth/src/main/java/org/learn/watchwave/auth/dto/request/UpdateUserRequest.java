package org.learn.watchwave.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 500, message = "Profile picture URL can be at most 500 characters")
    private String profilePicUrl;
}
