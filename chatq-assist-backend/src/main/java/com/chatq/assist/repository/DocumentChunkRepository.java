package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

    void deleteByDocumentId(Long documentId);

    long countByDocumentId(Long documentId);

    /**
     * Find document chunks by vector similarity using pgvector cosine distance
     * Returns top N most similar chunks for given embedding that meet the similarity threshold
     * Cosine distance: 0 = identical, 1 = orthogonal, 2 = opposite
     * We use 0.25 as threshold (corresponding to ~0.75 cosine similarity)
     */
    @Query(value = """
        SELECT * FROM document_chunks
        WHERE tenant_id = :tenantId
        AND embedding IS NOT NULL
        AND (embedding <=> CAST(:embedding AS vector)) < 0.25
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> findSimilarByEmbedding(
        @Param("tenantId") String tenantId,
        @Param("embedding") String embedding,
        @Param("limit") int limit
    );

    /**
     * Find document chunks by vector similarity for specific documents
     * Only returns completed documents with similarity above threshold
     */
    @Query(value = """
        SELECT dc.* FROM document_chunks dc
        INNER JOIN documents d ON dc.document_id = d.id
        WHERE dc.tenant_id = :tenantId
        AND d.status = 'COMPLETED'
        AND dc.embedding IS NOT NULL
        AND (dc.embedding <=> CAST(:embedding AS vector)) < 0.25
        ORDER BY dc.embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> findSimilarCompletedDocumentChunks(
        @Param("tenantId") String tenantId,
        @Param("embedding") String embedding,
        @Param("limit") int limit
    );
}
