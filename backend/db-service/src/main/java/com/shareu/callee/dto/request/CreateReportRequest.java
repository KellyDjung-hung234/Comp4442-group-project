package com.shareu.callee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotBlank(message = "targetType is required")
        @Pattern(regexp = "post|comment", message = "targetType must be post or comment")
        String targetType,
        @NotNull(message = "targetId is required")
        Long targetId,
        @NotBlank(message = "reason is required")
        @Size(min = 3, max = 64, message = "reason must be between 3 and 64 characters")
        String reason,
        @Size(max = 1000, message = "details must be <= 1000 characters")
        String details,
        @NotNull(message = "createdBy is required")
        Long createdBy
) {
}
