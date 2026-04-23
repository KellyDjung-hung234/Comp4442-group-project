ALTER TABLE users
    ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'password';

UPDATE users
SET password = 'admin123'
WHERE id = 1;

CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    text_content TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_comments_topic_created_at ON comments(topic_id, created_at DESC);
