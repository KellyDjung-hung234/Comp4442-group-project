package com.shareu.topic.domain.model;

import java.time.Instant;

public record Topic(
        Long id,
        String title,
        Long createdBy,
        String authorUsername,
        Long commentCount,
        Long likeCount,
        Long dislikeCount,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
