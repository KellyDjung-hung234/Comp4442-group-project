CREATE TABLE IF NOT EXISTS reactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type VARCHAR(16) NOT NULL COMMENT 'topic or comment',
    target_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction_type VARCHAR(16) NOT NULL COMMENT 'like or dislike',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target_reaction (target_type, target_id, user_id),
    CONSTRAINT fk_reactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_reactions_target ON reactions(target_type, target_id);
CREATE INDEX idx_reactions_user ON reactions(user_id);
