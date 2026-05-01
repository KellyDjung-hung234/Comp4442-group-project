package com.shareu.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RequestOtpRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 255, message = "username must be between 3 and 255 characters")
        String username,
        @Email(message = "email must be valid")
        @Size(max = 255, message = "email must be at most 255 characters")
        String email
) {
}
