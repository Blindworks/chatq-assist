-- Try to enable pgvector extension (will silently fail if not available)
DO $$
BEGIN
    CREATE EXTENSION IF NOT EXISTS vector;
EXCEPTION
    WHEN undefined_file THEN
        RAISE NOTICE 'pgvector extension not available - vector features will be disabled';
    WHEN SQLSTATE '0A000' THEN
        RAISE NOTICE 'pgvector extension not available - vector features will be disabled';
END
$$;

-- Add vector column for embeddings to faq_entries (only if extension exists)
-- OpenAI text-embedding-3-small produces 1536 dimensions
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector') THEN
        ALTER TABLE faq_entries ADD COLUMN embedding vector(1536);
        -- Create index for vector similarity search
        CREATE INDEX idx_faq_embedding ON faq_entries USING ivfflat (embedding vector_cosine_ops);
        RAISE NOTICE 'pgvector columns and indexes created successfully';
    ELSE
        RAISE NOTICE 'Skipping vector columns - pgvector extension not available';
    END IF;
END
$$;
