package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {

    Optional<MessageFeedback> findByMessageIdAndTenantId(Long messageId, String tenantId);

    boolean existsByMessageIdAndTenantId(Long messageId, String tenantId);
}
