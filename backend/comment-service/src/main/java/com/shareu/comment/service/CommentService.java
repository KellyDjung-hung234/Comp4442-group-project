package com.shareu.comment.service;

import com.shareu.comment.domain.model.Comment;
import com.shareu.comment.domain.repository.CommentRepository;
import com.shareu.comment.dto.request.CreateCommentRequest;
import com.shareu.comment.dto.response.CommentResponse;
import com.shareu.common.dto.PageResponse;
import com.shareu.common.exception.BadRequestException;
import com.shareu.common.exception.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final JdbcTemplate jdbcTemplate;

    public CommentService(CommentRepository commentRepository, JdbcTemplate jdbcTemplate) {
        this.commentRepository = commentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public CommentResponse createComment(long topicId, CreateCommentRequest request) {
        // Check if user is banned
        Boolean isBanned = false;
        try {
            isBanned = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(is_banned, FALSE) FROM users WHERE id = ?",
                    Boolean.class,
                    request.createdBy()
            );
        } catch (Exception e) {
            // User not found or other error, allow creation to proceed
        }

        if (isBanned) {
            throw new BadRequestException("Your account is banned. You cannot post or comment.");
        }

        Comment created = commentRepository.create(topicId, request.text().trim(), request.createdBy());
        
        // Increment comment count on topic
        try {
            jdbcTemplate.update(
                    "UPDATE topics SET comment_count = comment_count + 1 WHERE id = ?",
                    topicId
            );
        } catch (Exception e) {
            // Log but don't fail if comment count update fails
        }
        
        notifyTopicOwner(topicId, request.createdBy());
        return toResponse(created);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> listComments(long topicId, int page, int size) {
        validatePage(page, size);

        List<CommentResponse> content = commentRepository.findByTopicPage(topicId, page, size)
                .stream()
                .map(this::toResponse)
                .toList();

        long totalElements = commentRepository.countByTopicId(topicId);
        return new PageResponse<>(content, page, size, totalElements);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listCommentsByUser(long userId) {
        return commentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId, long topicId) {
        boolean deleted = commentRepository.deleteById(commentId);
        if (!deleted) {
            throw new NotFoundException("Comment not found");
        }
        jdbcTemplate.update(
                "UPDATE topics SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = ?",
                topicId
        );
    }

    private void validatePage(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("page must be >= 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("size must be between 1 and 100");
        }
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.id(),
                comment.topicId(),
                comment.text(),
                comment.createdBy(),
                comment.authorUsername(),
                comment.createdAt(),
                comment.updatedAt()
        );
    }

    private void notifyTopicOwner(long topicId, long commenterId) {
        Long topicOwnerId;
        try {
            topicOwnerId = jdbcTemplate.queryForObject(
                    "SELECT created_by FROM topics WHERE id = ?",
                    Long.class,
                    topicId
            );
        } catch (Exception e) {
            return;
        }

        if (topicOwnerId == null) {
            return;
        }

        String commenterUsername = jdbcTemplate.queryForObject(
                "SELECT username FROM users WHERE id = ?",
                String.class,
                commenterId
        );
        String message = "User " + (commenterUsername == null ? commenterId : commenterUsername) + " commented on your post.";
        jdbcTemplate.update(
                "INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, FALSE, NOW())",
                topicOwnerId,
                message
        );
    }
}
