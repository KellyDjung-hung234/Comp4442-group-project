package com.shareu.topic.service;

import com.shareu.topic.domain.model.Topic;
import com.shareu.topic.domain.repository.TopicRepository;
import com.shareu.topic.dto.request.CreateTopicRequest;
import com.shareu.topic.dto.response.TopicResponse;
import com.shareu.common.dto.PageResponse;
import com.shareu.common.exception.BadRequestException;
import com.shareu.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        String normalizedTitle = request.title().trim();

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
    public Topic getTopicById(long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));
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
