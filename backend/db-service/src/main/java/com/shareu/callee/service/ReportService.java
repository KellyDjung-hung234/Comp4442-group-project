package com.shareu.callee.service;

import com.shareu.callee.domain.repository.CommentRepository;
import com.shareu.callee.domain.repository.ReportRepository;
import com.shareu.callee.domain.repository.TopicRepository;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.request.CreateReportRequest;
import com.shareu.callee.exception.BadRequestException;
import com.shareu.callee.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository, TopicRepository topicRepository, CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void createReport(CreateReportRequest request) {
        if (!userRepository.existsById(request.createdBy())) {
            throw new BadRequestException("createdBy does not reference an existing user");
        }

        if ("post".equals(request.targetType())) {
            if (!topicRepository.existsById(request.targetId())) {
                throw new NotFoundException("Target post not found");
            }
        } else {
            if (!commentRepository.existsById(request.targetId())) {
                throw new NotFoundException("Target comment not found");
            }
        }

        String details = request.details() == null ? "" : request.details().trim();
        reportRepository.create(request.targetType(), request.targetId(), request.reason().trim(), details, request.createdBy());
    }
}
