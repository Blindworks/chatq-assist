-- Create message_feedback table for user feedback on assistant responses
CREATE TABLE message_feedback (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    tenant_id VARCHAR(255) NOT NULL,
    feedback_type VARCHAR(20) NOT NULL CHECK (feedback_type IN ('POSITIVE', 'NEGATIVE')),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,

    -- Ensure one feedback per message
    UNIQUE(message_id)
);

-- Index for querying feedback by tenant
CREATE INDEX idx_message_feedback_tenant ON message_feedback(tenant_id);

-- Index for querying feedback by message
CREATE INDEX idx_message_feedback_message ON message_feedback(message_id);

-- Index for analytics queries
CREATE INDEX idx_message_feedback_type ON message_feedback(feedback_type);
