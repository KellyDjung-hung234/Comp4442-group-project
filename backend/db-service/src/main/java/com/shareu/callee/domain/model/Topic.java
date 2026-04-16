package com.shareu.callee.domain.model;

import java.time.Instant;

public record Topic(
        Long id,
        String title,
        Long createdBy,
        Long commentCount,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
