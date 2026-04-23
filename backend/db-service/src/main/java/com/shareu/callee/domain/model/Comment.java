package com.shareu.callee.domain.model;

import java.time.Instant;

public record Comment(
        Long id,
        Long topicId,
        String text,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
