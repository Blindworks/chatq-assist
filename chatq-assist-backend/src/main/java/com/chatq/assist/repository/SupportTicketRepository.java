package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.SupportTicket;
import com.chatq.assist.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<SupportTicket> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, TicketStatus status);

    Long countByTenantIdAndStatus(String tenantId, TicketStatus status);

    Optional<SupportTicket> findByIdAndTenantId(Long id, String tenantId);

    Page<SupportTicket> findByTenantIdOrderByCreatedAtDesc(String tenantId, Pageable pageable);

    Page<SupportTicket> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, TicketStatus status, Pageable pageable);

    Long countByTenantId(String tenantId);
}
