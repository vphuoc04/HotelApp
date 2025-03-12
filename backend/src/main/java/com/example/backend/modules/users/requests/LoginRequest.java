package com.example.backend.modules.users.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not in correct format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
