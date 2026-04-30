package com.shareu.reaction.dto.response;

public record ReactionResponse(
        long likes,
        long dislikes,
        String currentReaction
) {
}
