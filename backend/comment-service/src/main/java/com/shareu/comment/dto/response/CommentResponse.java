package com.shareu.comment.dto.response;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

public record CommentResponse(
        Long id,
        Long topicId,
        String text,
        Long createdBy,
        String authorUsername,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Hong_Kong")
        Instant createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Hong_Kong")
        Instant updatedAt
) {
}
