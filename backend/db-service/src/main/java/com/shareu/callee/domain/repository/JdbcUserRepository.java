package com.shareu.callee.domain.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existsById(long userId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Long.class,
                userId
        );
        return count != null && count > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Long.class,
                username
        );
        return count != null && count > 0;
    }

    @Override
    public long create(String username, String displayName, String password) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, display_name, password) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, displayName);
            ps.setString(3, password);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to create user");
        }
        return key.longValue();
    }

    @Override
    public Optional<UserAuthRecord> findAuthByUsername(String username) {
        List<UserAuthRecord> rows = jdbcTemplate.query(
                "SELECT id, username, password FROM users WHERE username = ?",
                (rs, rowNum) -> new UserAuthRecord(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password")
                ),
                username
        );
        return rows.stream().findFirst();
    }
}
