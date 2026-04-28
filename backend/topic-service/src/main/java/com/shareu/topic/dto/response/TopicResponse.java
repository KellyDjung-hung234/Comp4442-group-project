package com.shareu.topic.dto.response;

import java.time.Instant;

public record TopicResponse(
        Long id,
        String title,
        Long createdBy,
        Long commentCount,
        Instant createdAt,
        Instant updatedAt
) {
}
