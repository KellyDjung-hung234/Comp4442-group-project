package com.shareu.callee.service;

import com.shareu.callee.domain.model.Comment;
import com.shareu.callee.domain.repository.CommentRepository;
import com.shareu.callee.domain.repository.TopicRepository;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.request.CreateCommentRequest;
import com.shareu.callee.dto.response.CommentResponse;
import com.shareu.callee.dto.response.PageResponse;
import com.shareu.callee.exception.BadRequestException;
import com.shareu.callee.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TopicRepository topicRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse createComment(long topicId, CreateCommentRequest request) {
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Topic not found");
        }
        if (!userRepository.existsById(request.createdBy())) {
            throw new BadRequestException("createdBy does not reference an existing user");
        }

        Comment created = commentRepository.create(topicId, request.text().trim(), request.createdBy());
        long count = commentRepository.countByTopicId(topicId);
        topicRepository.updateCommentCount(topicId, count);
        return toResponse(created);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> listComments(long topicId, int page, int size) {
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Topic not found");
        }
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
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Topic not found");
        }

        boolean deleted = commentRepository.deleteById(commentId);
        if (!deleted) {
            throw new NotFoundException("Comment not found");
        }

        long count = commentRepository.countByTopicId(topicId);
        topicRepository.updateCommentCount(topicId, count);
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
