-- Create conversations table
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_email VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    tenant_id VARCHAR(255) NOT NULL,
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_conversation_tenant_session ON conversations(tenant_id, session_id);
CREATE INDEX idx_conversation_tenant_status ON conversations(tenant_id, status);

-- Create messages table
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    confidence_score DOUBLE PRECISION,
    faq_entry_id BIGINT,
    metadata TEXT,
    tenant_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_faq FOREIGN KEY (faq_entry_id) REFERENCES faq_entries(id) ON DELETE SET NULL
);

CREATE INDEX idx_message_conversation ON messages(conversation_id);
CREATE INDEX idx_message_created ON messages(created_at);
