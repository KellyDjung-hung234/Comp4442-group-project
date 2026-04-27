package com.shareu.callee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 32, message = "username must be between 3 and 32 characters")
        String username,
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 20, message = "password must be between 8 and 20 characters")
        String password,
        @NotBlank(message = "otp is required")
        @Pattern(regexp = "\\d{6}", message = "otp must be 6 digits")
        String otp,
        boolean termsAccepted
) {
}
