package com.shareu.topic.domain.repository;

import com.shareu.topic.domain.model.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {

    Topic create(String title, long createdBy);

    Optional<Topic> findById(long topicId);

    boolean existsById(long topicId);

    List<Topic> findPage(int page, int size);

    List<Topic> findByCreatedBy(long createdBy, int page, int size);

    long countAll();

    long countByCreatedBy(long createdBy);

    void updateCommentCount(long topicId, long commentCount);

    void updateReactionCounts(long topicId, long likeCount, long dislikeCount);

    boolean deleteById(long topicId);
}
