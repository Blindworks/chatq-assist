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
     * Returns top N most similar chunks for given embedding
     */
    @Query(value = """
        SELECT * FROM document_chunks
        WHERE tenant_id = :tenantId
        AND embedding IS NOT NULL
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
     */
    @Query(value = """
        SELECT dc.* FROM document_chunks dc
        INNER JOIN documents d ON dc.document_id = d.id
        WHERE dc.tenant_id = :tenantId
        AND d.status = 'COMPLETED'
        AND dc.embedding IS NOT NULL
        ORDER BY dc.embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> findSimilarCompletedDocumentChunks(
        @Param("tenantId") String tenantId,
        @Param("embedding") String embedding,
        @Param("limit") int limit
    );
}
