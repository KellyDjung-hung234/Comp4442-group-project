package com.shareu.callee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTopicRequest(
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 120, message = "title must be between 3 and 120 characters")
        String title,
        @NotNull(message = "createdBy is required")
        Long createdBy
) {
}
