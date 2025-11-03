-- Create tenants table for multi-tenant SaaS management
CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL UNIQUE, -- The actual tenant identifier used throughout the system
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    domain VARCHAR(255), -- Optional: custom domain for widget embedding
    api_key VARCHAR(500) UNIQUE, -- API key for tenant-specific access
    is_active BOOLEAN NOT NULL DEFAULT true,
    max_users INTEGER DEFAULT 10, -- Subscription limit
    max_documents INTEGER DEFAULT 100, -- Subscription limit
    settings JSONB, -- Tenant-specific settings (colors, logo URL, etc.)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Add tenant reference to users table
ALTER TABLE users ADD COLUMN tenant_ref_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_tenant
    FOREIGN KEY (tenant_ref_id) REFERENCES tenants(id) ON DELETE CASCADE;

-- Update user roles to support RBAC
-- Current users.role is VARCHAR(50) with check constraint
-- We need to update the constraint to include new roles
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_user_role;
ALTER TABLE users ADD CONSTRAINT chk_user_role
    CHECK (role IN ('USER', 'ADMIN', 'SUPER_ADMIN', 'TENANT_ADMIN', 'TENANT_USER'));

-- Create indexes for efficient queries
CREATE INDEX idx_tenants_tenant_id ON tenants(tenant_id);
CREATE INDEX idx_tenants_is_active ON tenants(is_active);
CREATE INDEX idx_tenants_api_key ON tenants(api_key);
CREATE INDEX idx_users_tenant_ref ON users(tenant_ref_id);
CREATE INDEX idx_users_tenant_id_role ON users(tenant_id, role);

-- Create a default "system" tenant for super admins
INSERT INTO tenants (tenant_id, name, contact_email, is_active, max_users, max_documents, settings)
VALUES (
    'system',
    'System Administration',
    'admin@chatq-assist.local',
    true,
    999,
    999,
    '{"theme": "default"}'::jsonb
);

-- Update existing admin user to be SUPER_ADMIN and link to system tenant
UPDATE users
SET role = 'SUPER_ADMIN',
    tenant_ref_id = (SELECT id FROM tenants WHERE tenant_id = 'system')
WHERE username = 'admin';

-- Create a default customer tenant for demonstration
INSERT INTO tenants (tenant_id, name, contact_email, is_active, max_users, max_documents, settings)
VALUES (
    'default-tenant',
    'Demo Customer',
    'demo@example.com',
    true,
    10,
    100,
    '{"theme": "blue", "brandColor": "#007bff"}'::jsonb
);

-- Add comments for documentation
COMMENT ON TABLE tenants IS 'Stores tenant/customer information for multi-tenant SaaS model';
COMMENT ON COLUMN tenants.tenant_id IS 'Unique identifier used in X-Tenant-ID header and for data isolation';
COMMENT ON COLUMN tenants.api_key IS 'API key for programmatic access to tenant data';
COMMENT ON COLUMN tenants.settings IS 'JSON object with tenant-specific configurations (branding, limits, features)';
COMMENT ON COLUMN users.tenant_ref_id IS 'Foreign key reference to tenants table for user-tenant association';
