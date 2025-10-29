-- Create users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    enabled BOOLEAN NOT NULL DEFAULT true,
    tenant_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Index for login queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_tenant ON users(tenant_id);

-- Insert default admin user (password: admin123 - BCrypt hash)
-- Note: Change this password in production!
INSERT INTO users (username, password, email, role, tenant_id)
VALUES ('admin', '$2a$10$XPta3zqnE8eUqXQq9HqP4OvdPqWqH5MLqCPqGbXLLPLg8.TqKj5oy', 'admin@chatq.com', 'ADMIN', 'default-tenant');
