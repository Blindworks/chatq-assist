package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.Message;
import com.chatq.assist.domain.enums.MessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    // Analytics methods
    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c WHERE c.tenantId = :tenantId AND m.createdAt >= :startDate")
    Long countByTenantIdAndCreatedAtAfter(@Param("tenantId") String tenantId, @Param("startDate") Instant startDate);

    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c WHERE c.tenantId = :tenantId AND m.role = 'USER' AND m.createdAt >= :startDate")
    Long countUserMessagesByTenantAndDateAfter(@Param("tenantId") String tenantId, @Param("startDate") Instant startDate);

    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c WHERE m.faqEntryId = :faqId AND c.tenantId = :tenantId")
    Long countByFaqEntryIdAndTenantId(@Param("faqId") Long faqId, @Param("tenantId") String tenantId);

    @Query("SELECT AVG(m.confidenceScore) FROM Message m WHERE m.faqEntryId = :faqId AND m.confidenceScore IS NOT NULL")
    Double averageConfidenceByFaqEntryId(@Param("faqId") Long faqId);

    @Query(value = """
        SELECT CAST(m.created_at AS DATE) as date, COUNT(m.id) as count
        FROM messages m
        JOIN conversations c ON m.conversation_id = c.id
        WHERE c.tenant_id = :tenantId
        AND m.role = 'USER'
        AND m.created_at >= :startDate
        GROUP BY CAST(m.created_at AS DATE)
        ORDER BY date
        """, nativeQuery = true)
    List<Object[]> countUserMessagesByTenantGroupedByDate(@Param("tenantId") String tenantId, @Param("startDate") Instant startDate);
}
