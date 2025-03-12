package com.example.backend.modules.users.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlacklistedTokenRequest {
    @NotBlank(message = "Token can not be empty!")
    private String token;
}
