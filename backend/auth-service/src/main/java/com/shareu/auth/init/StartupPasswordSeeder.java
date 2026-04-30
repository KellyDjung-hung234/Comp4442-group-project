package com.shareu.auth.init;

import com.shareu.auth.domain.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupPasswordSeeder {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public StartupPasswordSeeder(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        // Ensure test users exist and have BCrypt-encoded passwords
        seedUserIfMissing("kelly", "123456", "ADMIN", "kelly@admin.com");
        seedUserIfMissing("kelly_user", "123456", "USER", "kelly_user@user.com");
        seedUserIfMissing("kwanloan", "123456", "ADMIN", "kwanloan@admin.com");
        seedUserIfMissing("kwanloan_user", "123456", "USER", "kwanloan_user@user.com");
        seedUserIfMissing("cheesring", "123456", "ADMIN", "cheesring@admin.com");
        seedUserIfMissing("cheesring_user", "123456", "USER", "cheesring_user@user.com");

        // Ensure user id 1 password is encoded if present
        try {
            String pwd1 = jdbcTemplate.queryForObject("SELECT password FROM users WHERE id = 1", String.class);
            if (pwd1 != null && !passwordEncoder.matches("123456", pwd1)) {
                jdbcTemplate.update("UPDATE users SET password = ? WHERE id = 1", passwordEncoder.encode("123456"));
            }
        } catch (Exception ignored) {
        }
    }

    private void seedUserIfMissing(String username, String rawPassword, String role, String email) {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
            if (count == null || count == 0) {
                String enc = passwordEncoder.encode(rawPassword);
                jdbcTemplate.update("INSERT INTO users (username, password, role, email, created_at) VALUES (?, ?, ?, ?, NOW())",
                        username, enc, role, email);
            } else {
                // Keep seeded test accounts on the documented shared password.
                String pwd = jdbcTemplate.queryForObject("SELECT password FROM users WHERE username = ?", String.class, username);
                if (pwd != null && !passwordEncoder.matches(rawPassword, pwd)) {
                    jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", passwordEncoder.encode(rawPassword), username);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
