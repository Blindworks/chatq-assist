package com.chatq.assist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for support ticket statistics.
 * Provides counts of tickets grouped by status for a specific tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatsDto {

    private Long totalTickets;

    private Long openTickets;

    private Long inProgressTickets;

    private Long resolvedTickets;

    private Long closedTickets;
}
