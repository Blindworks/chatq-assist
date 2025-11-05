-- Add customer question field to support tickets
ALTER TABLE support_tickets
ADD COLUMN customer_question TEXT;

COMMENT ON COLUMN support_tickets.customer_question IS 'The original question/message from the customer that triggered the handoff';
