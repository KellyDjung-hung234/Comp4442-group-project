package com.shareu.topic.service;

import com.shareu.topic.domain.model.Topic;
import com.shareu.topic.domain.repository.TopicRepository;
import com.shareu.topic.dto.request.CreateTopicRequest;
import com.shareu.topic.dto.response.TopicResponse;
import com.shareu.common.dto.PageResponse;
import com.shareu.common.exception.BadRequestException;
import com.shareu.common.exception.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final JdbcTemplate jdbcTemplate;

    public TopicService(TopicRepository topicRepository, JdbcTemplate jdbcTemplate) {
        this.topicRepository = topicRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        String normalizedTitle = request.title().trim();

        // Check if user is banned
        Boolean isBanned = false;
        try {
            isBanned = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(is_banned, FALSE) FROM users WHERE id = ?",
                    Boolean.class,
                    request.createdBy()
            );
        } catch (Exception e) {
            // User not found or other error, allow creation to proceed
        }

        if (isBanned) {
            throw new BadRequestException("Your account is banned. You cannot post or comment.");
        }

        Topic created = topicRepository.create(
                normalizedTitle,
                request.createdBy(),
                normalizeOptional(request.fileUrl()),
                normalizeOptional(request.fileType()),
                normalizeOptional(request.fileName())
        );
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

    @Transactional(readOnly = true)
    public PageResponse<TopicResponse> listMyPosts(long userId, int page, int size) {
        validatePage(page, size);

        List<TopicResponse> content = topicRepository.findByCreatedBy(userId, page, size)
                .stream()
                .map(this::toResponse)
                .toList();

        long totalElements = topicRepository.countByCreatedBy(userId);
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
                topic.authorUsername(),
                topic.commentCount(),
                topic.likeCount(),
                topic.dislikeCount(),
                topic.fileUrl(),
                topic.fileType(),
                topic.fileName(),
                topic.createdAt(),
                topic.updatedAt()
        );
    }

    @Transactional
    public void updateReactionCounts(long topicId, long likeCount, long dislikeCount) {
        if (likeCount < 0 || dislikeCount < 0) {
            throw new BadRequestException("reaction counts must be >= 0");
        }
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Topic not found");
        }
        topicRepository.updateReactionCounts(topicId, likeCount, dislikeCount);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
