-- Simple FAQ System

-- FAQ Entries
CREATE TABLE faq_entries (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    question VARCHAR(1000) NOT NULL,
    answer TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER,
    usage_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_faq_tenant_active ON faq_entries(tenant_id, is_active);

-- FAQ Tags
CREATE TABLE faq_tags (
    faq_id BIGINT NOT NULL REFERENCES faq_entries(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL,
    PRIMARY KEY (faq_id, tag)
);

CREATE INDEX idx_faq_tags_tag ON faq_tags(tag);
