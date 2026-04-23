package com.shareu.callee.domain.repository;

import com.shareu.callee.domain.model.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTopicRepository implements TopicRepository {

    private static final RowMapper<Topic> TOPIC_ROW_MAPPER = (rs, rowNum) -> new Topic(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getLong("created_by"),
            rs.getLong("comment_count"),
            rs.getLong("version"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcTopicRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Topic create(String title, long createdBy) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO topics (title, created_by) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, title);
            ps.setLong(2, createdBy);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to create topic");
        }

        return findById(key.longValue())
                .orElseThrow(() -> new IllegalStateException("Created topic not found"));
    }

    @Override
    public Optional<Topic> findById(long topicId) {
        List<Topic> rows = jdbcTemplate.query(
                "SELECT id, title, created_by, comment_count, version, created_at, updated_at FROM topics WHERE id = ?",
                TOPIC_ROW_MAPPER,
                topicId
        );
        return rows.stream().findFirst();
    }

    @Override
    public boolean existsById(long topicId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM topics WHERE id = ?",
                Long.class,
                topicId
        );
        return count != null && count > 0;
    }

    @Override
    public List<Topic> findPage(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                "SELECT id, title, created_by, comment_count, version, created_at, updated_at FROM topics ORDER BY created_at DESC LIMIT ? OFFSET ?",
                TOPIC_ROW_MAPPER,
                size,
                offset
        );
    }

    @Override
    public long countAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM topics", Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public void updateCommentCount(long topicId, long commentCount) {
        jdbcTemplate.update(
                "UPDATE topics SET comment_count = ? WHERE id = ?",
                commentCount,
                topicId
        );
    }

    @Override
    public boolean deleteById(long topicId) {
        int affected = jdbcTemplate.update("DELETE FROM topics WHERE id = ?", topicId);
        return affected > 0;
    }
}
