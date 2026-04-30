ALTER TABLE reports
    ADD COLUMN reported_by BIGINT NULL;

UPDATE reports
SET reported_by = created_by
WHERE reported_by IS NULL;

ALTER TABLE reports
    ADD CONSTRAINT fk_reports_reported_by FOREIGN KEY (reported_by) REFERENCES users(id);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(255) NOT NULL,
    details VARCHAR(1000) NULL,
    action_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read, created_at DESC);