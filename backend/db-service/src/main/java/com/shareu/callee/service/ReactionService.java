package com.shareu.callee.service;

import com.shareu.callee.domain.repository.CommentRepository;
import com.shareu.callee.domain.repository.TopicRepository;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.request.ToggleReactionRequest;
import com.shareu.callee.dto.response.ReactionResponse;
import com.shareu.callee.exception.BadRequestException;
import com.shareu.callee.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReactionService {

    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private final Map<String, String> topicUserReactions = new ConcurrentHashMap<>();
    private final Map<String, String> commentUserReactions = new ConcurrentHashMap<>();
    private final Map<Long, long[]> topicCounts = new ConcurrentHashMap<>();
    private final Map<Long, long[]> commentCounts = new ConcurrentHashMap<>();

    public ReactionService(TopicRepository topicRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public ReactionResponse toggleTopicReaction(long topicId, ToggleReactionRequest request) {
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Topic not found");
        }
        validateUser(request.userId());

        String key = topicId + ":" + request.userId();
        long[] counts = topicCounts.computeIfAbsent(topicId, ignored -> new long[]{0L, 0L});
        String current = topicUserReactions.get(key);
        String next = applyToggle(current, request.type(), counts);

        if (next == null) {
            topicUserReactions.remove(key);
        } else {
            topicUserReactions.put(key, next);
        }

        return new ReactionResponse(counts[0], counts[1], next);
    }

    public ReactionResponse toggleCommentReaction(long commentId, ToggleReactionRequest request) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment not found");
        }
        validateUser(request.userId());

        String key = commentId + ":" + request.userId();
        long[] counts = commentCounts.computeIfAbsent(commentId, ignored -> new long[]{0L, 0L});
        String current = commentUserReactions.get(key);
        String next = applyToggle(current, request.type(), counts);

        if (next == null) {
            commentUserReactions.remove(key);
        } else {
            commentUserReactions.put(key, next);
        }

        return new ReactionResponse(counts[0], counts[1], next);
    }

    private void validateUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("userId does not reference an existing user");
        }
    }

    private String applyToggle(String current, String requested, long[] counts) {
        if (requested.equals(current)) {
            decrement(counts, current);
            return null;
        }

        decrement(counts, current);
        increment(counts, requested);
        return requested;
    }

    private void increment(long[] counts, String reaction) {
        if ("like".equals(reaction)) {
            counts[0]++;
        } else if ("dislike".equals(reaction)) {
            counts[1]++;
        }
    }

    private void decrement(long[] counts, String reaction) {
        if ("like".equals(reaction) && counts[0] > 0) {
            counts[0]--;
        } else if ("dislike".equals(reaction) && counts[1] > 0) {
            counts[1]--;
        }
    }
}
