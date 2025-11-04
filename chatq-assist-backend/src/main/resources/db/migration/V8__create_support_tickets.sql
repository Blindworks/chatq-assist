-- Create support_tickets table for handoff requests
CREATE TABLE support_tickets (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    conversation_id BIGINT,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_to VARCHAR(255),
    notes TEXT,

    CONSTRAINT fk_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE SET NULL
);

-- Create indexes for common queries
CREATE INDEX idx_support_tickets_tenant_id ON support_tickets(tenant_id);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);
CREATE INDEX idx_support_tickets_created_at ON support_tickets(created_at DESC);
CREATE INDEX idx_support_tickets_conversation_id ON support_tickets(conversation_id);

-- Add comments
COMMENT ON TABLE support_tickets IS 'Support tickets created when chat handoff is triggered';
COMMENT ON COLUMN support_tickets.status IS 'Ticket status: OPEN, IN_PROGRESS, RESOLVED, CLOSED';
COMMENT ON COLUMN support_tickets.priority IS 'Ticket priority: LOW, MEDIUM, HIGH, URGENT';
