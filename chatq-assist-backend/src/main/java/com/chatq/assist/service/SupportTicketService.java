package com.chatq.assist.service;

import com.chatq.assist.domain.dto.HandoffRequestDto;
import com.chatq.assist.domain.entity.Conversation;
import com.chatq.assist.domain.entity.SupportTicket;
import com.chatq.assist.domain.enums.TicketPriority;
import com.chatq.assist.domain.enums.TicketStatus;
import com.chatq.assist.repository.ConversationRepository;
import com.chatq.assist.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final ConversationRepository conversationRepository;
    private final EmailService emailService;

    @Transactional
    public SupportTicket createHandoffTicket(HandoffRequestDto request, String tenantId) {
        log.info("Creating handoff ticket for sessionId: {}, tenant: {}, email: {}",
                request.getSessionId(), tenantId, request.getEmail());

        // Find conversation by sessionId
        Optional<Conversation> conversationOpt = conversationRepository.findBySessionId(request.getSessionId());

        if (conversationOpt.isEmpty()) {
            log.warn("Conversation not found for sessionId: {}", request.getSessionId());
        }

        // Create ticket
        SupportTicket ticket = SupportTicket.builder()
                .tenantId(tenantId)
                .conversation(conversationOpt.orElse(null))
                .customerName(request.getName())
                .customerEmail(request.getEmail())
                .customerPhone(request.getPhone())
                .status(TicketStatus.OPEN)
                .priority(TicketPriority.MEDIUM)
                .build();

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        log.info("Created support ticket with ID: {}", savedTicket.getId());

        // Send email notification to admin
        try {
            emailService.sendHandoffNotification(savedTicket);
        } catch (Exception e) {
            log.error("Failed to send handoff notification email", e);
            // Don't fail the ticket creation if email fails
        }

        return savedTicket;
    }

    public List<SupportTicket> getTicketsByTenant(String tenantId) {
        return supportTicketRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    public List<SupportTicket> getTicketsByTenantAndStatus(String tenantId, TicketStatus status) {
        return supportTicketRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status);
    }
}
