package com.shareu.comment.domain.repository;

import com.shareu.comment.domain.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
            rs.getString("author_username"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final String COMMENT_SELECT =
            "SELECT c.id, c.topic_id, c.text_content, c.created_by, COALESCE(u.username, CONCAT('User #', c.created_by)) AS author_username, " +
                    "c.created_at, c.updated_at FROM comments c LEFT JOIN users u ON u.id = c.created_by ";

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
                COMMENT_SELECT + "WHERE c.id = ? AND c.is_deleted = FALSE",
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
                COMMENT_SELECT + "WHERE c.topic_id = ? AND c.is_deleted = FALSE ORDER BY c.created_at DESC LIMIT ? OFFSET ?",
                COMMENT_ROW_MAPPER,
                topicId,
                size,
                offset
        );
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return namedParameterJdbcTemplate.query(
                COMMENT_SELECT + "WHERE c.created_by = :userId AND c.is_deleted = FALSE ORDER BY c.created_at DESC",
                new MapSqlParameterSource("userId", userId),
                COMMENT_ROW_MAPPER
        );
    }

    @Override
    public boolean existsById(long commentId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comments WHERE id = ? AND is_deleted = FALSE",
                Long.class,
                commentId
        );
        return count != null && count > 0;
    }

    @Override
    public long countByTopicId(long topicId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comments WHERE topic_id = ? AND is_deleted = FALSE",
                Long.class,
                topicId
        );
        return count == null ? 0L : count;
    }

    @Override
    public boolean deleteById(long commentId) {
        int affected = jdbcTemplate.update(
                "UPDATE comments SET is_deleted = TRUE, deleted_at = NOW() WHERE id = ? AND is_deleted = FALSE",
                commentId
        );
        return affected > 0;
    }
}
