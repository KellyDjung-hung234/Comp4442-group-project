package com.shareu.topic.dto.response;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

public record TopicResponse(
        Long id,
        String title,
        Long createdBy,
        String authorUsername,
        Long commentCount,
        Long likeCount,
        Long dislikeCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Hong_Kong")
        Instant createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Hong_Kong")
        Instant updatedAt
) {
}
