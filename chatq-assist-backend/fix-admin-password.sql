-- Fix admin password
-- This sets the password to 'admin123' with a fresh BCrypt hash
-- BCrypt hash generated with strength 10 for password 'admin123'

UPDATE users
SET password = '$2a$10$XPta3zqnE8eUqXQq9HqP4OvdPqWqH5MLqCPqGbXLLPLg8.TqKj5oy'
WHERE username = 'admin';

-- Verify the update
SELECT username, role, enabled,
       CASE
           WHEN password = '$2a$10$XPta3zqnE8eUqXQq9HqP4OvdPqWqH5MLqCPqGbXLLPLg8.TqKj5oy'
           THEN 'Password updated correctly'
           ELSE 'Password does not match'
       END as password_status
FROM users
WHERE username = 'admin';
