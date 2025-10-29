package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.Conversation;
import com.chatq.assist.domain.enums.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findBySessionIdAndTenantId(String sessionId, String tenantId);

    List<Conversation> findByTenantIdAndStatus(String tenantId, ConversationStatus status);

    // Analytics methods
    long countByTenantIdAndCreatedAtAfter(String tenantId, Instant startDate);

    long countByTenantIdAndStatus(String tenantId, ConversationStatus status);
}
