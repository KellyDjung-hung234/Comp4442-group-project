package com.shareu.callee.api;

import com.shareu.callee.dto.request.ToggleReactionRequest;
import com.shareu.callee.dto.response.ReactionResponse;
import com.shareu.callee.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reactions")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("/topics/{topicId}")
    public ReactionResponse toggleTopicReaction(@PathVariable long topicId, @Valid @RequestBody ToggleReactionRequest request) {
        return reactionService.toggleTopicReaction(topicId, request);
    }

    @PostMapping("/comments/{commentId}")
    public ReactionResponse toggleCommentReaction(@PathVariable long commentId, @Valid @RequestBody ToggleReactionRequest request) {
        return reactionService.toggleCommentReaction(commentId, request);
    }
}
