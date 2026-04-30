package com.shareu.topic.domain.repository;

import com.shareu.topic.domain.model.Topic;
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
            rs.getString("author_username"),
            rs.getLong("comment_count"),
            rs.getLong("like_count"),
            rs.getLong("dislike_count"),
            rs.getLong("version"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    private final JdbcTemplate jdbcTemplate;
    private static final String TOPIC_SELECT =
            "SELECT t.id, t.title, t.created_by, COALESCE(u.username, CONCAT('User #', t.created_by)) AS author_username, " +
                    "t.comment_count, t.like_count, t.dislike_count, t.version, t.created_at, t.updated_at " +
                    "FROM topics t LEFT JOIN users u ON u.id = t.created_by ";

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
                TOPIC_SELECT + "WHERE t.id = ? AND t.is_deleted = FALSE",
                TOPIC_ROW_MAPPER,
                topicId
        );
        return rows.stream().findFirst();
    }

    @Override
    public boolean existsById(long topicId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM topics WHERE id = ? AND is_deleted = FALSE",
                Long.class,
                topicId
        );
        return count != null && count > 0;
    }

    @Override
    public List<Topic> findPage(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                TOPIC_SELECT + "WHERE t.is_deleted = FALSE ORDER BY t.created_at DESC LIMIT ? OFFSET ?",
                TOPIC_ROW_MAPPER,
                size,
                offset
        );
    }

    @Override
    public List<Topic> findByCreatedBy(long createdBy, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                TOPIC_SELECT + "WHERE t.created_by = ? AND t.is_deleted = FALSE ORDER BY t.created_at DESC LIMIT ? OFFSET ?",
                TOPIC_ROW_MAPPER,
                createdBy,
                size,
                offset
        );
    }

    @Override
    public long countAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM topics WHERE is_deleted = FALSE", Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public long countByCreatedBy(long createdBy) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM topics WHERE created_by = ? AND is_deleted = FALSE",
                Long.class,
                createdBy
        );
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
    public void updateReactionCounts(long topicId, long likeCount, long dislikeCount) {
        jdbcTemplate.update(
                "UPDATE topics SET like_count = ?, dislike_count = ? WHERE id = ?",
                likeCount,
                dislikeCount,
                topicId
        );
    }

    @Override
    public boolean deleteById(long topicId) {
        int affected = jdbcTemplate.update(
                "UPDATE topics SET is_deleted = TRUE, deleted_at = NOW() WHERE id = ? AND is_deleted = FALSE",
                topicId
        );
        return affected > 0;
    }
}
