package com.shareu.topic.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateReactionCountsRequest(
        @NotNull(message = "likeCount is required")
        @PositiveOrZero(message = "likeCount must be >= 0")
        Long likeCount,
        @NotNull(message = "dislikeCount is required")
        @PositiveOrZero(message = "dislikeCount must be >= 0")
        Long dislikeCount
) {
}
