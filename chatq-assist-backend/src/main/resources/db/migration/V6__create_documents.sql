-- Create documents table for PDF/URL content storage
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    title VARCHAR(500) NOT NULL,
    source_url TEXT,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    file_path VARCHAR(1000), -- Path to stored file on disk/S3
    file_size BIGINT, -- File size in bytes
    mime_type VARCHAR(100),
    chunk_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_document_type CHECK (document_type IN ('URL', 'PDF', 'DOCX', 'TXT', 'SITEMAP')),
    CONSTRAINT chk_document_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

-- Create document_chunks table for storing text chunks with embeddings
CREATE TABLE document_chunks (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    document_id BIGINT NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536), -- OpenAI text-embedding-3-small dimension
    token_count INTEGER,
    metadata JSONB, -- Store additional metadata like page number, headers, etc.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_chunks_document FOREIGN KEY (document_id)
        REFERENCES documents(id) ON DELETE CASCADE
);

-- Create indexes for efficient queries
CREATE INDEX idx_documents_tenant_status ON documents(tenant_id, status);
CREATE INDEX idx_documents_tenant_type ON documents(tenant_id, document_type);
CREATE INDEX idx_documents_created ON documents(created_at DESC);

CREATE INDEX idx_document_chunks_document ON document_chunks(document_id);
CREATE INDEX idx_document_chunks_tenant ON document_chunks(tenant_id);

-- Create vector similarity search index for document chunks
CREATE INDEX idx_document_chunks_embedding ON document_chunks
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- Add comment for documentation
COMMENT ON TABLE documents IS 'Stores uploaded documents (PDFs, URLs, etc.) with processing status';
COMMENT ON TABLE document_chunks IS 'Stores text chunks extracted from documents with vector embeddings for RAG';
COMMENT ON COLUMN document_chunks.embedding IS 'Vector embedding for semantic search (OpenAI text-embedding-3-small, 1536 dimensions)';
