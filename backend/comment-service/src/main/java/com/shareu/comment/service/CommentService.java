package com.shareu.comment.service;

import com.shareu.comment.domain.model.Comment;
import com.shareu.comment.domain.repository.CommentRepository;
import com.shareu.comment.dto.request.CreateCommentRequest;
import com.shareu.comment.dto.response.CommentResponse;
import com.shareu.common.dto.PageResponse;
import com.shareu.common.exception.BadRequestException;
import com.shareu.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResponse createComment(long topicId, CreateCommentRequest request) {
        Comment created = commentRepository.create(topicId, request.text().trim(), request.createdBy());
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

    @Transactional
    public void deleteComment(long commentId, long topicId) {
        boolean deleted = commentRepository.deleteById(commentId);
        if (!deleted) {
            throw new NotFoundException("Comment not found");
        }
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
                comment.createdAt(),
                comment.updatedAt()
        );
    }
}
