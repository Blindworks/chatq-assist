package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.TicketPriority;
import com.chatq.assist.domain.enums.TicketStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating support ticket information.
 * All fields are optional to support partial updates.
 */
@Data
public class UpdateTicketDto {

    private TicketStatus status;

    private TicketPriority priority;

    @Size(max = 255, message = "Assigned to field must not exceed 255 characters")
    private String assignedTo;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}
