package com.shareu.callee.api;

import com.shareu.callee.dto.request.CreateReportRequest;
import com.shareu.callee.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Void> createReport(@Valid @RequestBody CreateReportRequest request) {
        reportService.createReport(request);
        return ResponseEntity.accepted().build();
    }
}
