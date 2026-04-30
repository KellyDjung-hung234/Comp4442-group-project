-- add role column to users table
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- first user is the admin
UPDATE users SET role = 'ADMIN' WHERE id = 1;
