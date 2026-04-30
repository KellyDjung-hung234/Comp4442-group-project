package com.shareu.comment.domain.model;

import java.time.Instant;

public record Comment(
        Long id,
        Long topicId,
        String text,
        Long createdBy,
        String authorUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
