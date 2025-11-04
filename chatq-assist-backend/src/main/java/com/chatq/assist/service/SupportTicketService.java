package com.chatq.assist.service;

import com.chatq.assist.domain.dto.HandoffRequestDto;
import com.chatq.assist.domain.dto.TicketResponseDto;
import com.chatq.assist.domain.dto.TicketStatsDto;
import com.chatq.assist.domain.dto.UpdateTicketDto;
import com.chatq.assist.domain.entity.Conversation;
import com.chatq.assist.domain.entity.SupportTicket;
import com.chatq.assist.domain.enums.TicketPriority;
import com.chatq.assist.domain.enums.TicketStatus;
import com.chatq.assist.exception.TicketNotFoundException;
import com.chatq.assist.repository.ConversationRepository;
import com.chatq.assist.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        // Find conversation by sessionId and tenantId
        Optional<Conversation> conversationOpt = conversationRepository.findBySessionIdAndTenantId(
                request.getSessionId(), tenantId);

        if (conversationOpt.isEmpty()) {
            log.warn("Conversation not found for sessionId: {} and tenantId: {}",
                    request.getSessionId(), tenantId);
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

    /**
     * Get paginated tickets for a tenant with optional status filter
     */
    public Page<TicketResponseDto> getTickets(String tenantId, TicketStatus status, Pageable pageable) {
        log.info("Fetching tickets for tenant: {}, status: {}, page: {}", tenantId, status, pageable.getPageNumber());

        Page<SupportTicket> tickets;
        if (status != null) {
            tickets = supportTicketRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status, pageable);
        } else {
            tickets = supportTicketRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable);
        }

        return tickets.map(this::convertToResponseDto);
    }

    /**
     * Get a single ticket by ID with tenant validation
     */
    public TicketResponseDto getTicketById(Long id, String tenantId) {
        log.info("Fetching ticket with ID: {} for tenant: {}", id, tenantId);

        SupportTicket ticket = supportTicketRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found - ID: {}, tenant: {}", id, tenantId);
                    return new TicketNotFoundException(id, tenantId);
                });

        return convertToResponseDto(ticket);
    }

    /**
     * Update an existing ticket
     */
    @Transactional
    public TicketResponseDto updateTicket(Long id, UpdateTicketDto updateDto, String tenantId) {
        log.info("Updating ticket ID: {} for tenant: {}", id, tenantId);

        SupportTicket ticket = supportTicketRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found for update - ID: {}, tenant: {}", id, tenantId);
                    return new TicketNotFoundException(id, tenantId);
                });

        // Update only non-null fields (partial update)
        if (updateDto.getStatus() != null) {
            ticket.setStatus(updateDto.getStatus());
            log.debug("Updated ticket #{} status to: {}", id, updateDto.getStatus());
        }

        if (updateDto.getPriority() != null) {
            ticket.setPriority(updateDto.getPriority());
            log.debug("Updated ticket #{} priority to: {}", id, updateDto.getPriority());
        }

        if (updateDto.getAssignedTo() != null) {
            ticket.setAssignedTo(updateDto.getAssignedTo());
            log.debug("Updated ticket #{} assigned to: {}", id, updateDto.getAssignedTo());
        }

        if (updateDto.getNotes() != null) {
            ticket.setNotes(updateDto.getNotes());
            log.debug("Updated ticket #{} notes", id);
        }

        // updatedAt is automatically updated by @PreUpdate in entity
        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        log.info("Successfully updated ticket #{}", id);

        return convertToResponseDto(savedTicket);
    }

    /**
     * Delete a ticket
     */
    @Transactional
    public void deleteTicket(Long id, String tenantId) {
        log.info("Deleting ticket ID: {} for tenant: {}", id, tenantId);

        SupportTicket ticket = supportTicketRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found for deletion - ID: {}, tenant: {}", id, tenantId);
                    return new TicketNotFoundException(id, tenantId);
                });

        supportTicketRepository.delete(ticket);
        log.info("Successfully deleted ticket #{}", id);
    }

    /**
     * Get ticket statistics for a tenant
     */
    public TicketStatsDto getTicketStats(String tenantId) {
        log.info("Fetching ticket statistics for tenant: {}", tenantId);

        Long totalTickets = supportTicketRepository.countByTenantId(tenantId);
        Long openTickets = supportTicketRepository.countByTenantIdAndStatus(tenantId, TicketStatus.OPEN);
        Long inProgressTickets = supportTicketRepository.countByTenantIdAndStatus(tenantId, TicketStatus.IN_PROGRESS);
        Long resolvedTickets = supportTicketRepository.countByTenantIdAndStatus(tenantId, TicketStatus.RESOLVED);
        Long closedTickets = supportTicketRepository.countByTenantIdAndStatus(tenantId, TicketStatus.CLOSED);

        TicketStatsDto stats = TicketStatsDto.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .build();

        log.info("Ticket statistics - total: {}, open: {}, in_progress: {}, resolved: {}, closed: {}",
                totalTickets, openTickets, inProgressTickets, resolvedTickets, closedTickets);

        return stats;
    }

    /**
     * Convert SupportTicket entity to TicketResponseDto
     */
    private TicketResponseDto convertToResponseDto(SupportTicket ticket) {
        String sessionId = null;
        if (ticket.getConversation() != null) {
            sessionId = ticket.getConversation().getSessionId();
        }

        return TicketResponseDto.builder()
                .id(ticket.getId())
                .tenantId(ticket.getTenantId())
                .sessionId(sessionId)
                .customerName(ticket.getCustomerName())
                .customerEmail(ticket.getCustomerEmail())
                .customerPhone(ticket.getCustomerPhone())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .assignedTo(ticket.getAssignedTo())
                .notes(ticket.getNotes())
                .build();
    }
}
