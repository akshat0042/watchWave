package org.learn.watchwave.auth.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @Size(max = 10, message = "Gender can be at most 10 characters")
    private String gender;

    private LocalDate birthdate;

    @Size(max = 100, message = "Location can be at most 100 characters")
    private String location;

    @Size(max = 500, message = "Bio can be at most 500 characters")
    private String bio;
}
