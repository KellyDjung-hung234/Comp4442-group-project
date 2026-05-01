package com.shareu.topic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTopicRequest(
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 120, message = "title must be between 3 and 120 characters")
        String title,
        @NotNull(message = "createdBy is required")
        Long createdBy,
        String fileUrl,
        @Size(max = 100, message = "fileType must be at most 100 characters")
        String fileType,
        @Size(max = 255, message = "fileName must be at most 255 characters")
        String fileName
) {
}
