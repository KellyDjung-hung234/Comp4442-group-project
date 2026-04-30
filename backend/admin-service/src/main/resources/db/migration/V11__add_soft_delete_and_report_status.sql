ALTER TABLE topics
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at TIMESTAMP NULL;

CREATE INDEX idx_topics_deleted_created_at ON topics(is_deleted, created_at DESC);

ALTER TABLE comments
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at TIMESTAMP NULL;

CREATE INDEX idx_comments_topic_deleted_created_at ON comments(topic_id, is_deleted, created_at DESC);

ALTER TABLE reports
    ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    ADD COLUMN handled_at TIMESTAMP NULL;

CREATE INDEX idx_reports_status_created_at ON reports(status, created_at DESC);
