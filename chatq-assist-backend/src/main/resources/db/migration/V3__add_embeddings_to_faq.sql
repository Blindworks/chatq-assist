-- Enable pgvector extension first
CREATE EXTENSION IF NOT EXISTS vector;

-- Add vector column for embeddings to faq_entries
-- OpenAI text-embedding-3-small produces 1536 dimensions
ALTER TABLE faq_entries ADD COLUMN embedding vector(1536);

-- Create index for vector similarity search
CREATE INDEX idx_faq_embedding ON faq_entries USING ivfflat (embedding vector_cosine_ops);
