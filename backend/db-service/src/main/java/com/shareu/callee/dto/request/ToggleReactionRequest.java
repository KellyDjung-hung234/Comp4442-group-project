package com.shareu.callee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ToggleReactionRequest(
        @NotNull(message = "userId is required")
        Long userId,
        @NotBlank(message = "type is required")
        @Pattern(regexp = "like|dislike", message = "type must be like or dislike")
        String type
) {
}
