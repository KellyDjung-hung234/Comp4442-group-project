package com.shareu.callee.domain.repository;

import com.shareu.callee.domain.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcCommentRepository implements CommentRepository {

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("topic_id"),
            rs.getString("text_content"),
            rs.getLong("created_by"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Comment create(long topicId, String text, long createdBy) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comments (topic_id, text_content, created_by) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, topicId);
            ps.setString(2, text);
            ps.setLong(3, createdBy);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to create comment");
        }

        List<Comment> rows = jdbcTemplate.query(
                "SELECT id, topic_id, text_content, created_by, created_at, updated_at FROM comments WHERE id = ?",
                COMMENT_ROW_MAPPER,
                key.longValue()
        );
        if (rows.isEmpty()) {
            throw new IllegalStateException("Created comment not found");
        }
        return rows.get(0);
    }

    @Override
    public List<Comment> findByTopicPage(long topicId, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                "SELECT id, topic_id, text_content, created_by, created_at, updated_at FROM comments WHERE topic_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                COMMENT_ROW_MAPPER,
                topicId,
                size,
                offset
        );
    }

    @Override
    public boolean existsById(long commentId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comments WHERE id = ?",
                Long.class,
                commentId
        );
        return count != null && count > 0;
    }

    @Override
    public long countByTopicId(long topicId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comments WHERE topic_id = ?",
                Long.class,
                topicId
        );
        return count == null ? 0L : count;
    }

    @Override
    public boolean deleteById(long commentId) {
        int affected = jdbcTemplate.update("DELETE FROM comments WHERE id = ?", commentId);
        return affected > 0;
    }
}
