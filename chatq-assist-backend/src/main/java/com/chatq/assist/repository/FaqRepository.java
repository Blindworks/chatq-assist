package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.FaqEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntry, Long> {
    List<FaqEntry> findByTenantIdAndIsActiveOrderByDisplayOrderAsc(String tenantId, Boolean isActive);
    List<FaqEntry> findByTenantIdOrderByDisplayOrderAsc(String tenantId);
    List<FaqEntry> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find FAQs by vector similarity using pgvector cosine distance
     * Returns top N most similar FAQs for given embedding
     */
    @Query(value = """
        SELECT * FROM faq_entries
        WHERE tenant_id = :tenantId
        AND is_active = true
        AND embedding IS NOT NULL
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<FaqEntry> findSimilarByEmbedding(
        @Param("tenantId") String tenantId,
        @Param("embedding") String embedding,
        @Param("limit") int limit
    );
}
