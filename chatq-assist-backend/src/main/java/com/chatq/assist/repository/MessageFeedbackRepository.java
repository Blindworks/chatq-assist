package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {

    Optional<MessageFeedback> findByMessageIdAndTenantId(Long messageId, String tenantId);

    boolean existsByMessageIdAndTenantId(Long messageId, String tenantId);

    // Analytics methods
    List<MessageFeedback> findByTenantIdAndCreatedAtAfter(String tenantId, Instant startDate);

    @Query(value = """
        SELECT mf.*
        FROM message_feedback mf
        JOIN messages m ON mf.message_id = m.id
        JOIN conversations c ON m.conversation_id = c.id
        WHERE m.faq_entry_id = :faqId
        AND c.tenant_id = :tenantId
        """, nativeQuery = true)
    List<MessageFeedback> findByMessageFaqEntryId(@Param("faqId") Long faqId, @Param("tenantId") String tenantId);
}
