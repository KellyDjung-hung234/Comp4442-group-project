package com.shareu.comment.domain.repository;

import com.shareu.comment.domain.model.Comment;

import java.util.List;

public interface CommentRepository {

    Comment create(long topicId, String text, long createdBy);

    List<Comment> findByTopicPage(long topicId, int page, int size);

    List<Comment> findByUserId(Long userId);

    boolean existsById(long commentId);

    long countByTopicId(long topicId);

    boolean deleteById(long commentId);
}
