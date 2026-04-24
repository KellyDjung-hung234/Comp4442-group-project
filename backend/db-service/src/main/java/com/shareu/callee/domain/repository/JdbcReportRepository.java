package com.shareu.callee.domain.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcReportRepository implements ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(String targetType, long targetId, String reason, String details, long createdBy) {
        jdbcTemplate.update(
                "INSERT INTO reports (target_type, target_id, reason, details, created_by) VALUES (?, ?, ?, ?, ?)",
                targetType,
                targetId,
                reason,
                details,
                createdBy
        );
    }
}
