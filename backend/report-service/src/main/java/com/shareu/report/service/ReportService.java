package com.shareu.report.service;

import com.shareu.report.domain.repository.ReportRepository;
import com.shareu.report.dto.request.CreateReportRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void createReport(CreateReportRequest request) {
        String details = request.details() == null ? "" : request.details().trim();
        reportRepository.create(request.targetType(), request.targetId(), request.reason().trim(), details, request.createdBy());
    }
}
