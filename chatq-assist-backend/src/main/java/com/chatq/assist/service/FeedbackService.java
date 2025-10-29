package com.chatq.assist.service;

import com.chatq.assist.domain.dto.FeedbackRequest;
import com.chatq.assist.domain.entity.MessageFeedback;
import com.chatq.assist.repository.MessageFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final MessageFeedbackRepository feedbackRepository;

    /**
     * Submit or update feedback for a message
     */
    @Transactional
    public MessageFeedback submitFeedback(FeedbackRequest request, String tenantId) {
        log.info("Submitting feedback for messageId: {}, type: {}, tenant: {}",
                 request.getMessageId(), request.getFeedbackType(), tenantId);

        // Check if feedback already exists
        Optional<MessageFeedback> existing = feedbackRepository
            .findByMessageIdAndTenantId(request.getMessageId(), tenantId);

        MessageFeedback feedback;

        if (existing.isPresent()) {
            // Update existing feedback
            feedback = existing.get();
            feedback.setFeedbackType(request.getFeedbackType());
            feedback.setComment(request.getComment());
            log.info("Updated existing feedback id: {}", feedback.getId());
        } else {
            // Create new feedback
            feedback = new MessageFeedback();
            feedback.setMessageId(request.getMessageId());
            feedback.setTenantId(tenantId);
            feedback.setFeedbackType(request.getFeedbackType());
            feedback.setComment(request.getComment());
            log.info("Created new feedback for message: {}", request.getMessageId());
        }

        return feedbackRepository.save(feedback);
    }

    /**
     * Check if feedback exists for a message
     */
    public boolean hasFeedback(Long messageId, String tenantId) {
        return feedbackRepository.existsByMessageIdAndTenantId(messageId, tenantId);
    }

    /**
     * Get feedback for a message
     */
    public Optional<MessageFeedback> getFeedback(Long messageId, String tenantId) {
        return feedbackRepository.findByMessageIdAndTenantId(messageId, tenantId);
    }
}
