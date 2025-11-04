package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.TicketResponseDto;
import com.chatq.assist.domain.dto.TicketStatsDto;
import com.chatq.assist.domain.dto.UpdateTicketDto;
import com.chatq.assist.domain.enums.TicketStatus;
import com.chatq.assist.exception.TicketNotFoundException;
import com.chatq.assist.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Support Ticket Management.
 * Provides endpoints for listing, viewing, updating, and deleting support tickets.
 * All endpoints require authentication and enforce tenant isolation.
 */
@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class SupportTicketController {

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    private final SupportTicketService supportTicketService;

    /**
     * Get all tickets for a tenant with optional status filter and pagination.
     *
     * @param tenantId Tenant identifier from header
     * @param status Optional filter by ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
     * @param page Page number (0-indexed, default: 0)
     * @param size Page size (default: 20)
     * @return Paginated list of tickets
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<TicketResponseDto>> getAllTickets(
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/tickets - tenant: {}, status: {}, page: {}, size: {}",
                tenantId, status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponseDto> tickets = supportTicketService.getTickets(tenantId, status, pageable);

        log.info("Returning {} tickets for tenant: {}", tickets.getNumberOfElements(), tenantId);

        return ResponseEntity.ok(tickets);
    }

    /**
     * Get a single ticket by ID.
     *
     * @param id Ticket ID
     * @param tenantId Tenant identifier from header
     * @return Ticket details if found and belongs to tenant
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TicketResponseDto> getTicketById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("GET /api/tickets/{} - tenant: {}", id, tenantId);

        try {
            TicketResponseDto ticket = supportTicketService.getTicketById(id, tenantId);
            return ResponseEntity.ok(ticket);
        } catch (TicketNotFoundException e) {
            log.warn("Ticket not found or access denied - ID: {}, tenant: {}", id, tenantId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update an existing ticket.
     * Supports partial updates - only provided fields will be updated.
     *
     * @param id Ticket ID
     * @param updateDto Update data (all fields optional)
     * @param tenantId Tenant identifier from header
     * @return Updated ticket details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TicketResponseDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketDto updateDto,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("PUT /api/tickets/{} - tenant: {}, updateDto: {}", id, tenantId, updateDto);

        try {
            TicketResponseDto updatedTicket = supportTicketService.updateTicket(id, updateDto, tenantId);
            return ResponseEntity.ok(updatedTicket);
        } catch (TicketNotFoundException e) {
            log.warn("Failed to update ticket - ID: {}, tenant: {}, error: {}", id, tenantId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a ticket.
     *
     * @param id Ticket ID
     * @param tenantId Tenant identifier from header
     * @return 204 No Content on success, 404 Not Found if ticket doesn't exist or doesn't belong to tenant
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("DELETE /api/tickets/{} - tenant: {}", id, tenantId);

        try {
            supportTicketService.deleteTicket(id, tenantId);
            log.info("Successfully deleted ticket #{}", id);
            return ResponseEntity.noContent().build();
        } catch (TicketNotFoundException e) {
            log.warn("Failed to delete ticket - ID: {}, tenant: {}, error: {}", id, tenantId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get ticket statistics for the tenant.
     * Returns counts grouped by status.
     *
     * @param tenantId Tenant identifier from header
     * @return Statistics including total, open, in_progress, resolved, and closed ticket counts
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TicketStatsDto> getTicketStats(
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("GET /api/tickets/stats - tenant: {}", tenantId);

        TicketStatsDto stats = supportTicketService.getTicketStats(tenantId);

        log.info("Returning ticket stats for tenant: {} - total: {}, open: {}, in_progress: {}, resolved: {}, closed: {}",
                tenantId, stats.getTotalTickets(), stats.getOpenTickets(),
                stats.getInProgressTickets(), stats.getResolvedTickets(), stats.getClosedTickets());

        return ResponseEntity.ok(stats);
    }
}
