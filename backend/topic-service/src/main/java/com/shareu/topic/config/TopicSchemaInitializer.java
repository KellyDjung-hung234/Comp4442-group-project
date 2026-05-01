package com.shareu.topic.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TopicSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    public TopicSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureMediaColumns() {
        addColumnIfMissing("file_url", "MEDIUMTEXT NULL");
        addColumnIfMissing("file_type", "VARCHAR(100) NULL");
        addColumnIfMissing("file_name", "VARCHAR(255) NULL");
    }

    private void addColumnIfMissing(String columnName, String definition) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'topics' AND COLUMN_NAME = ?",
                Long.class,
                columnName
        );
        if (count == null || count == 0) {
            jdbcTemplate.execute("ALTER TABLE topics ADD COLUMN " + columnName + " " + definition);
        }
    }
}
