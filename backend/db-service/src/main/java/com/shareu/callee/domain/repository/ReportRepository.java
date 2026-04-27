package com.shareu.callee.domain.repository;

public interface ReportRepository {

    void create(String targetType, long targetId, String reason, String details, long createdBy);
}
