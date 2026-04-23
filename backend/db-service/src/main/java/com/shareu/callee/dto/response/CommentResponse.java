package com.shareu.callee.dto.response;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long topicId,
        String text,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
