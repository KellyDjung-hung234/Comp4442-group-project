package com.shareu.callee.domain.repository;

import com.shareu.callee.domain.model.Comment;

import java.util.List;

public interface CommentRepository {

    Comment create(long topicId, String text, long createdBy);

    List<Comment> findByTopicPage(long topicId, int page, int size);

    boolean existsById(long commentId);

    long countByTopicId(long topicId);

    boolean deleteById(long commentId);
}
