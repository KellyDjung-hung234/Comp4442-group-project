package com.shareu.callee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestOtpRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 32, message = "username must be between 3 and 32 characters")
        String username
) {
}
