package com.chatq.assist.service;

import com.chatq.assist.domain.entity.SupportTicket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.admin}")
    private String emailAdmin;

    @Value("${email.enabled:true}")
    private boolean emailEnabled;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Sends an email notification to admins when a handoff is triggered.
     * Uses HTML email template for professional appearance.
     *
     * @param ticket The support ticket created from handoff request
     */
    public void sendHandoffNotification(SupportTicket ticket) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Logging notification instead.");
            logHandoffNotification(ticket);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(emailAdmin);
            helper.setSubject("🎫 New Support Ticket #" + ticket.getId() + " - Customer Needs Assistance");

            String htmlContent = buildHandoffEmailHtml(ticket);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Handoff notification email sent successfully for ticket #{}", ticket.getId());

        } catch (MessagingException e) {
            log.error("Failed to send handoff notification email for ticket #{}", ticket.getId(), e);
            // Fallback to logging if email fails
            logHandoffNotification(ticket);
        }
    }

    /**
     * Builds HTML email content for handoff notification
     */
    private String buildHandoffEmailHtml(SupportTicket ticket) {
        String sessionId = ticket.getConversation() != null
            ? ticket.getConversation().getSessionId()
            : "N/A";

        String formattedDate = ticket.getCreatedAt() != null
            ? DATE_FORMATTER.format(ticket.getCreatedAt().atZone(java.time.ZoneId.systemDefault()))
            : "N/A";

        String customerName = ticket.getCustomerName() != null ? ticket.getCustomerName() : "Not provided";
        String customerPhone = ticket.getCustomerPhone() != null ? ticket.getCustomerPhone() : "Not provided";
        String customerQuestion = ticket.getCustomerQuestion() != null ? ticket.getCustomerQuestion() : "Not provided";

        return "<!DOCTYPE html>" +
            "<html lang=\"de\">" +
            "<head>" +
            "    <meta charset=\"UTF-8\">" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "    <title>New Support Ticket</title>" +
            "</head>" +
            "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;\">" +
            "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">" +
            "        <tr>" +
            "            <td align=\"center\" style=\"padding: 40px 0;\">" +
            "                <table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); border-radius: 8px; overflow: hidden;\">" +
            "                    <!-- Header -->" +
            "                    <tr>" +
            "                        <td style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;\">" +
            "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px; font-weight: 600;\">" +
            "                                🎫 New Support Ticket" +
            "                            </h1>" +
            "                            <p style=\"margin: 10px 0 0 0; color: #e0e7ff; font-size: 16px;\">" +
            "                                A customer needs your assistance" +
            "                            </p>" +
            "                        </td>" +
            "                    </tr>" +
            "                    <!-- Ticket ID Badge -->" +
            "                    <tr>" +
            "                        <td style=\"padding: 0;\">" +
            "                            <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; border-bottom: 1px solid #e2e8f0;\">" +
            "                                <span style=\"display: inline-block; background-color: #667eea; color: white; padding: 8px 20px; border-radius: 20px; font-weight: 600; font-size: 14px;\">" +
            "                                    Ticket #" + ticket.getId() +
            "                                </span>" +
            "                            </div>" +
            "                        </td>" +
            "                    </tr>" +
            "                    <!-- Content -->" +
            "                    <tr>" +
            "                        <td style=\"padding: 30px;\">" +
            "                            <!-- Customer Information -->" +
            "                            <div style=\"margin-bottom: 25px;\">" +
            "                                <h2 style=\"margin: 0 0 15px 0; color: #1e293b; font-size: 18px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px;\">" +
            "                                    👤 Customer Information" +
            "                                </h2>" +
            "                                <table style=\"width: 100%; border-collapse: collapse;\">" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500; width: 120px;\">Name:</td>" +
            "                                        <td style=\"padding: 8px 0; color: #1e293b; font-weight: 600;\">" + customerName + "</td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Email:</td>" +
            "                                        <td style=\"padding: 8px 0;\">" +
            "                                            <a href=\"mailto:" + ticket.getCustomerEmail() + "\" style=\"color: #667eea; text-decoration: none;\">" + ticket.getCustomerEmail() + "</a>" +
            "                                        </td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Phone:</td>" +
            "                                        <td style=\"padding: 8px 0; color: #1e293b;\">" + customerPhone + "</td>" +
            "                                    </tr>" +
            "                                </table>" +
            "                            </div>" +
            "                            <!-- Customer Question -->" +
            "                            <div style=\"margin-bottom: 25px;\">" +
            "                                <h2 style=\"margin: 0 0 15px 0; color: #1e293b; font-size: 18px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px;\">" +
            "                                    💬 Customer Question" +
            "                                </h2>" +
            "                                <div style=\"background-color: #f8fafc; padding: 15px; border-left: 4px solid #667eea; border-radius: 4px;\">" +
            "                                    <p style=\"margin: 0; color: #1e293b; font-size: 14px; line-height: 1.6;\">" + customerQuestion + "</p>" +
            "                                </div>" +
            "                            </div>" +
            "                            <!-- Ticket Details -->" +
            "                            <div style=\"margin-bottom: 25px;\">" +
            "                                <h2 style=\"margin: 0 0 15px 0; color: #1e293b; font-size: 18px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px;\">" +
            "                                    📋 Ticket Details" +
            "                                </h2>" +
            "                                <table style=\"width: 100%; border-collapse: collapse;\">" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500; width: 120px;\">Status:</td>" +
            "                                        <td style=\"padding: 8px 0;\">" +
            "                                            <span style=\"background-color: #dbeafe; color: #1e40af; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;\">" +
            "                                                " + ticket.getStatus() +
            "                                            </span>" +
            "                                        </td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Priority:</td>" +
            "                                        <td style=\"padding: 8px 0;\">" +
            "                                            <span style=\"background-color: #fed7aa; color: #92400e; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;\">" +
            "                                                " + ticket.getPriority() +
            "                                            </span>" +
            "                                        </td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Created:</td>" +
            "                                        <td style=\"padding: 8px 0; color: #1e293b;\">" + formattedDate + "</td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Tenant:</td>" +
            "                                        <td style=\"padding: 8px 0; color: #1e293b;\">" + ticket.getTenantId() + "</td>" +
            "                                    </tr>" +
            "                                    <tr>" +
            "                                        <td style=\"padding: 8px 0; color: #64748b; font-weight: 500;\">Session ID:</td>" +
            "                                        <td style=\"padding: 8px 0; color: #1e293b; font-family: monospace; font-size: 12px;\">" + sessionId + "</td>" +
            "                                    </tr>" +
            "                                </table>" +
            "                            </div>" +
            "                            <!-- Call to Action -->" +
            "                            <div style=\"text-align: center; margin-top: 30px;\">" +
            "                                <a href=\"http://localhost:4200/admin/tickets\"" +
            "                                   style=\"display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; padding: 14px 32px; border-radius: 6px; font-weight: 600; font-size: 16px; box-shadow: 0 4px 6px rgba(102, 126, 234, 0.3);\">" +
            "                                    View Ticket in Dashboard →" +
            "                                </a>" +
            "                            </div>" +
            "                        </td>" +
            "                    </tr>" +
            "                    <!-- Footer -->" +
            "                    <tr>" +
            "                        <td style=\"background-color: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0;\">" +
            "                            <p style=\"margin: 0; color: #64748b; font-size: 12px;\">" +
            "                                ChatQ Assist - Support Ticket Management System" +
            "                            </p>" +
            "                            <p style=\"margin: 5px 0 0 0; color: #94a3b8; font-size: 11px;\">" +
            "                                This is an automated notification. Please do not reply to this email." +
            "                            </p>" +
            "                        </td>" +
            "                    </tr>" +
            "                </table>" +
            "            </td>" +
            "        </tr>" +
            "    </table>" +
            "</body>" +
            "</html>";
    }

    /**
     * Logs handoff notification to console (fallback when email is disabled or fails)
     */
    private void logHandoffNotification(SupportTicket ticket) {
        log.info("=== HANDOFF NOTIFICATION ===");
        log.info("Support Ticket #{} created", ticket.getId());
        log.info("Customer: {} ({})", ticket.getCustomerName(), ticket.getCustomerEmail());
        log.info("Phone: {}", ticket.getCustomerPhone());
        log.info("Question: {}", ticket.getCustomerQuestion());
        log.info("Tenant: {}", ticket.getTenantId());
        log.info("Status: {}", ticket.getStatus());
        log.info("Priority: {}", ticket.getPriority());
        log.info("Created: {}", ticket.getCreatedAt());

        if (ticket.getConversation() != null) {
            log.info("Conversation ID: {}", ticket.getConversation().getId());
            log.info("Session ID: {}", ticket.getConversation().getSessionId());
        }

        log.info("============================");
    }
}
