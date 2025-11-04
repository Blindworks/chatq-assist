package com.chatq.assist.exception;

/**
 * Exception thrown when a support ticket is not found or access is denied.
 * This can occur when the ticket ID doesn't exist or doesn't belong to the requesting tenant.
 */
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(Long ticketId) {
        super("Ticket not found with ID: " + ticketId);
    }

    public TicketNotFoundException(Long ticketId, String tenantId) {
        super(String.format("Ticket not found with ID: %d for tenant: %s", ticketId, tenantId));
    }
}
