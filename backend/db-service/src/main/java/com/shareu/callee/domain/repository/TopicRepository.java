package com.shareu.callee.domain.repository;

import com.shareu.callee.domain.model.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {

    Topic create(String title, long createdBy);

    Optional<Topic> findById(long topicId);

    List<Topic> findPage(int page, int size);

    long countAll();

    boolean deleteById(long topicId);
}
