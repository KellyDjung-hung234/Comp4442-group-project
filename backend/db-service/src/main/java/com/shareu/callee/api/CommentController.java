package com.shareu.callee.api;

import com.shareu.callee.dto.request.CreateCommentRequest;
import com.shareu.callee.dto.response.CommentResponse;
import com.shareu.callee.dto.response.PageResponse;
import com.shareu.callee.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/topics/{topicId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable long topicId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = commentService.createComment(topicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/topics/{topicId}/comments")
    public PageResponse<CommentResponse> listComments(
            @PathVariable long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return commentService.listComments(topicId, page, size);
    }

    @DeleteMapping("/topics/{topicId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long topicId, @PathVariable long commentId) {
        commentService.deleteComment(commentId, topicId);
        return ResponseEntity.noContent().build();
    }
}
