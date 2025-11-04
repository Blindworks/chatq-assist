package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.TicketPriority;
import com.chatq.assist.domain.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for support ticket responses.
 * Includes sessionId from conversation but not the full conversation object
 * to avoid circular references and unnecessary data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {

    private Long id;

    private String tenantId;

    private String sessionId; // From associated conversation

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private TicketStatus status;

    private TicketPriority priority;

    private Instant createdAt;

    private Instant updatedAt;

    private String assignedTo;

    private String notes;
}
