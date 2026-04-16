package com.shareu.callee.service;

import com.shareu.callee.domain.model.Topic;
import com.shareu.callee.domain.repository.TopicRepository;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.response.TopicResponse;
import com.shareu.callee.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public TopicService(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        String normalizedTitle = request.title().trim();
        if (!userRepository.existsById(request.createdBy())) {
            throw new BadRequestException("createdBy does not reference an existing user");
        }

        Topic created = topicRepository.create(normalizedTitle, request.createdBy());
        return toResponse(created);
    }

    @Transactional(readOnly = true)
    public TopicResponse getTopic(long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));
        return toResponse(topic);
    }

    private TopicResponse toResponse(Topic topic) {
        return new TopicResponse(
                topic.id(),
                topic.title(),
                topic.createdBy(),
                topic.commentCount(),
                topic.createdAt(),
                topic.updatedAt()
        );
    }
}
