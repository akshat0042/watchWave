package org.learn.watchwave.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    // Optional profile fields
    @Size(max = 10, message = "Gender can be at most 10 characters")
    private String gender;

    private LocalDate birthdate;

    @Size(max = 100, message = "Location can be at most 100 characters")
    private String location;

    @Size(max = 500, message = "Bio can be at most 500 characters")
    private String bio;
}
