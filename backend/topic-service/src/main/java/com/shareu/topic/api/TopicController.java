package com.shareu.topic.api;

import com.shareu.topic.domain.model.Topic;
import com.shareu.topic.dto.request.CreateTopicRequest;
import com.shareu.common.dto.PageResponse;
import com.shareu.topic.dto.response.TopicResponse;
import com.shareu.topic.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        TopicResponse response = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public PageResponse<TopicResponse> listTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return topicService.listTopics(page, size);
    }

    @GetMapping("/{topicId}")
    public TopicResponse getTopic(@PathVariable long topicId) {
        return topicService.getTopic(topicId);
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(
        @PathVariable long topicId,
        @RequestHeader("X-User-Id") long currentUserId,
        @RequestHeader("X-User-Role") String role 
    ) {
    //1. get the topic by id
    Topic topic = topicService.getTopicById(topicId);

    // 2. check if the current user is the creator of the topic or an admin
    if (topic.createdBy() != currentUserId && !"ADMIN".equals(role)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); //denied if not creator and not admin
    }

    // 3. delete the topic
    topicService.deleteTopic(topicId);
    return ResponseEntity.noContent().build();
    }
}
