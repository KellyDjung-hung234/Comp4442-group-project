package com.shareu.comment.domain.model;

import java.time.Instant;

public record Comment(
        Long id,
        Long topicId,
        String text,
        Long createdBy,
        String authorUsername,
        String fileUrl,
        String fileType,
        String fileName,
        Instant createdAt,
        Instant updatedAt
) {
}
