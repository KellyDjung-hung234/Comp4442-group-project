package com.shareu.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "text is required")
        @Size(min = 1, max = 1000, message = "text must be between 1 and 1000 characters")
        String text,
        @NotNull(message = "createdBy is required")
        Long createdBy,
        String fileUrl,
        @Size(max = 100, message = "fileType must be at most 100 characters")
        String fileType,
        @Size(max = 255, message = "fileName must be at most 255 characters")
        String fileName
) {
}
