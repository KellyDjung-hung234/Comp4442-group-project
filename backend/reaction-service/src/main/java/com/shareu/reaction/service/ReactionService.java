package com.shareu.reaction.service;

import com.shareu.reaction.dto.request.ToggleReactionRequest;
import com.shareu.reaction.dto.response.ReactionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * ReactionService manages likes and dislikes for topics and comments.
 * Uses database persistence (reactions table) instead of in-memory storage.
 * This ensures counts survive service restarts and are shared across instances.
 */
@Service
public class ReactionService {

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final String topicServiceBaseUrl;

    public ReactionService(
            JdbcTemplate jdbcTemplate,
            @Value("${shareu.topic-service.url:http://localhost:8082/api/v1/topics}") String topicServiceBaseUrl
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = new RestTemplate();
        this.topicServiceBaseUrl = topicServiceBaseUrl;
    }

    @Transactional
    public ReactionResponse toggleTopicReaction(long topicId, ToggleReactionRequest request) {
        ReactionResponse response = toggleReaction("topic", topicId, request);
        updateTopicReactionCounts(topicId, response.likes(), response.dislikes());
        return response;
    }

    @Transactional
    public ReactionResponse toggleCommentReaction(long commentId, ToggleReactionRequest request) {
        return toggleReaction("comment", commentId, request);
    }

    private ReactionResponse toggleReaction(String targetType, long targetId, ToggleReactionRequest request) {
        // Check if user already has a reaction.
        List<String> existingReactions = jdbcTemplate.queryForList(
                "SELECT reaction_type FROM reactions WHERE target_type = ? AND target_id = ? AND user_id = ?",
                String.class,
                targetType, targetId, request.userId()
        );
        String currentReaction = existingReactions.isEmpty() ? null : existingReactions.get(0);

        String newReaction = null;
        if (currentReaction != null) {
            // If same reaction, remove it (toggle off)
            if (currentReaction.equals(request.type())) {
                jdbcTemplate.update(
                        "DELETE FROM reactions WHERE target_type = ? AND target_id = ? AND user_id = ?",
                        targetType, targetId, request.userId()
                );
            } else {
                // Update to new reaction type
                jdbcTemplate.update(
                        "UPDATE reactions SET reaction_type = ?, updated_at = NOW() WHERE target_type = ? AND target_id = ? AND user_id = ?",
                        request.type(), targetType, targetId, request.userId()
                );
                newReaction = request.type();
            }
        } else if ("like".equals(request.type()) || "dislike".equals(request.type())) {
            // Add new reaction
            jdbcTemplate.update(
                    "INSERT INTO reactions (target_type, target_id, user_id, reaction_type, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())",
                    targetType, targetId, request.userId(), request.type()
            );
            newReaction = request.type();
        }

        // Get current counts
        Long likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reactions WHERE target_type = ? AND target_id = ? AND reaction_type = 'like'",
                Long.class,
                targetType, targetId
        );
        if (likeCount == null) likeCount = 0L;

        Long dislikeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reactions WHERE target_type = ? AND target_id = ? AND reaction_type = 'dislike'",
                Long.class,
                targetType, targetId
        );
        if (dislikeCount == null) dislikeCount = 0L;

        return new ReactionResponse(likeCount, dislikeCount, newReaction);
    }

    private void updateTopicReactionCounts(long topicId, long likeCount, long dislikeCount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Long>> request = new HttpEntity<>(
                Map.of("likeCount", likeCount, "dislikeCount", dislikeCount),
                headers
        );

        restTemplate.postForEntity(topicServiceBaseUrl + "/" + topicId + "/reaction-counts", request, Void.class);
    }
}
