UPDATE users
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
WHERE username IN (
    'admin',
    'kelly',
    'kelly_user',
    'kwanloan',
    'kwanloan_user',
    'cheesring',
    'cheesring_user'
);
