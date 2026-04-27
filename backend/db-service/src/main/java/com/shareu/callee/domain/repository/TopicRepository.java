package com.shareu.callee.domain.repository;

import com.shareu.callee.domain.model.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {

    Topic create(String title, long createdBy);

    Optional<Topic> findById(long topicId);

    boolean existsById(long topicId);

    List<Topic> findPage(int page, int size);

    long countAll();

    void updateCommentCount(long topicId, long commentCount);

    boolean deleteById(long topicId);
}
