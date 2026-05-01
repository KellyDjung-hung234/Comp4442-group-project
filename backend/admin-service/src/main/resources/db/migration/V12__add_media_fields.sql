SET @topic_file_url_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'topics'
      AND COLUMN_NAME = 'file_url'
);
SET @sql := IF(@topic_file_url_exists = 0, 'ALTER TABLE topics ADD COLUMN file_url MEDIUMTEXT NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @topic_file_type_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'topics'
      AND COLUMN_NAME = 'file_type'
);
SET @sql := IF(@topic_file_type_exists = 0, 'ALTER TABLE topics ADD COLUMN file_type VARCHAR(100) NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @topic_file_name_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'topics'
      AND COLUMN_NAME = 'file_name'
);
SET @sql := IF(@topic_file_name_exists = 0, 'ALTER TABLE topics ADD COLUMN file_name VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @comment_file_url_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'comments'
      AND COLUMN_NAME = 'file_url'
);
SET @sql := IF(@comment_file_url_exists = 0, 'ALTER TABLE comments ADD COLUMN file_url MEDIUMTEXT NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @comment_file_type_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'comments'
      AND COLUMN_NAME = 'file_type'
);
SET @sql := IF(@comment_file_type_exists = 0, 'ALTER TABLE comments ADD COLUMN file_type VARCHAR(100) NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @comment_file_name_exists := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'comments'
      AND COLUMN_NAME = 'file_name'
);
SET @sql := IF(@comment_file_name_exists = 0, 'ALTER TABLE comments ADD COLUMN file_name VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
