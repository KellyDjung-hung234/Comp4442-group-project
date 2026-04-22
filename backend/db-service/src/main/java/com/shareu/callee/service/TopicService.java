package com.shareu.callee.service;

import com.shareu.callee.domain.model.Topic;
import com.shareu.callee.domain.repository.TopicRepository;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.request.CreateTopicRequest;
import com.shareu.callee.dto.response.PageResponse;
import com.shareu.callee.dto.response.TopicResponse;
import com.shareu.callee.exception.BadRequestException;
import com.shareu.callee.exception.NotFoundException;
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

    @Transactional(readOnly = true)
    public PageResponse<TopicResponse> listTopics(int page, int size) {
        validatePage(page, size);

        List<TopicResponse> content = topicRepository.findPage(page, size)
                .stream()
                .map(this::toResponse)
                .toList();

        long totalElements = topicRepository.countAll();
        return new PageResponse<>(content, page, size, totalElements);
    }

    @Transactional
    public void deleteTopic(long topicId) {
        boolean deleted = topicRepository.deleteById(topicId);
        if (!deleted) {
            throw new NotFoundException("Topic not found");
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

