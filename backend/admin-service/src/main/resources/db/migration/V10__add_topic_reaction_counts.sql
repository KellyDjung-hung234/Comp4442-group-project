ALTER TABLE topics
    ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN dislike_count BIGINT NOT NULL DEFAULT 0;

UPDATE topics t
SET like_count = (
        SELECT COUNT(*)
        FROM reactions r
        WHERE r.target_type = 'topic'
          AND r.target_id = t.id
          AND r.reaction_type = 'like'
    ),
    dislike_count = (
        SELECT COUNT(*)
        FROM reactions r
        WHERE r.target_type = 'topic'
          AND r.target_id = t.id
          AND r.reaction_type = 'dislike'
    );
