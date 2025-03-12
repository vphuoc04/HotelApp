package com.example.backend.modules.users.requests.UserCatalogue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Publish status is requried")
    @Min(value = 0, message = "Status value must be greater than or equal to 0")
    @Max(value = 2, message = "Status value must be less than or equal to 2")
    private Integer publish;
}
