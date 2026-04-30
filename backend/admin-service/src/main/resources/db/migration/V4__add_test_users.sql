SET @email_exists := (
SELECT COUNT(*)
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'users'
  AND COLUMN_NAME = 'email'
);

SET @sql := IF(
@email_exists = 0,
'ALTER TABLE users ADD COLUMN email VARCHAR(255)',
'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Insert only the required test users for the project
INSERT IGNORE INTO users (username, password, display_name, role, email, created_at) VALUES
('kelly', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Kelly Admin', 'ADMIN', 'kelly@admin.com', NOW()),
('kelly_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Kelly User', 'USER', 'kelly_user@user.com', NOW()),
('kwanloan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Kwanloan Admin', 'ADMIN', 'kwanloan@admin.com', NOW()),
('kwanloan_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Kwanloan User', 'USER', 'kwanloan_user@user.com', NOW()),
('cheesring', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Cheesring Admin', 'ADMIN', 'cheesring@admin.com', NOW()),
('cheesring_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Cheesring User', 'USER', 'cheesring_user@user.com', NOW());

-- If a legacy admin exists at id 1, ensure its password is set.
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' WHERE id = 1;
