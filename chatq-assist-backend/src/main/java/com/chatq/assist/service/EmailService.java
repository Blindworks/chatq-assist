package com.chatq.assist.service;

import com.chatq.assist.domain.entity.SupportTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    /**
     * Sends an email notification to admins when a handoff is triggered.
     *
     * TODO: Implement actual email sending using JavaMailSender or external email service.
     * For now, this just logs the notification.
     *
     * Configuration needed:
     * - spring.mail.host
     * - spring.mail.port
     * - spring.mail.username
     * - spring.mail.password
     * - Admin email addresses
     */
    public void sendHandoffNotification(SupportTicket ticket) {
        log.info("=== HANDOFF NOTIFICATION ===");
        log.info("Support Ticket #{} created", ticket.getId());
        log.info("Customer: {} ({})", ticket.getCustomerName(), ticket.getCustomerEmail());
        log.info("Phone: {}", ticket.getCustomerPhone());
        log.info("Tenant: {}", ticket.getTenantId());
        log.info("Status: {}", ticket.getStatus());
        log.info("Created: {}", ticket.getCreatedAt());

        if (ticket.getConversation() != null) {
            log.info("Conversation ID: {}", ticket.getConversation().getId());
            log.info("Session ID: {}", ticket.getConversation().getSessionId());
        }

        log.info("============================");

        // TODO: Replace with actual email sending
        // Example implementation:
        /*
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("admin@example.com");
        message.setSubject("New Support Ticket #" + ticket.getId());
        message.setText(String.format(
            "A customer needs assistance.\n\n" +
            "Customer: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n\n" +
            "Ticket ID: %d\n" +
            "Created: %s\n\n" +
            "Please log in to the admin dashboard to view the conversation history.",
            ticket.getCustomerName(),
            ticket.getCustomerEmail(),
            ticket.getCustomerPhone(),
            ticket.getId(),
            ticket.getCreatedAt()
        ));
        mailSender.send(message);
        */
    }
}
