package org.learn.watchwave.auth.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String profilePicUrl;
    private boolean isBlocked;
    private boolean isVerified;
    private List<String> roles;

    // Profile data
    private String gender;
    private LocalDate birthdate;
    private String location;
    private String bio;
}
