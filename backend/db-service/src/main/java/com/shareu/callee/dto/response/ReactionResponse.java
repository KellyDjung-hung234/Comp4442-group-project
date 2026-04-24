package com.shareu.callee.dto.response;

public record ReactionResponse(
        long likes,
        long dislikes,
        String currentReaction
) {
}
