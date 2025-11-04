package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.SupportTicket;
import com.chatq.assist.domain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<SupportTicket> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, TicketStatus status);

    Long countByTenantIdAndStatus(String tenantId, TicketStatus status);
}
