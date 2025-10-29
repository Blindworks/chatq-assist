package com.chatq.assist.service;

import com.chatq.assist.domain.dto.AnalyticsDto;
import com.chatq.assist.domain.entity.FaqEntry;
import com.chatq.assist.domain.entity.MessageFeedback;
import com.chatq.assist.domain.enums.ConversationStatus;
import com.chatq.assist.domain.enums.FeedbackType;
import com.chatq.assist.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository feedbackRepository;
    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public AnalyticsDto getAnalytics(String tenantId, Integer daysBack) {
        log.info("Generating analytics for tenant: {}, daysBack: {}", tenantId, daysBack);

        if (daysBack == null || daysBack <= 0) {
            daysBack = 30; // Default to last 30 days
        }

        Instant startDate = Instant.now().minus(daysBack, ChronoUnit.DAYS);

        return AnalyticsDto.builder()
                .feedbackMetrics(calculateFeedbackMetrics(tenantId, startDate))
                .topFaqs(calculateTopFaqs(tenantId, 10))
                .conversationMetrics(calculateConversationMetrics(tenantId, startDate))
                .questionsByDate(calculateQuestionsByDate(tenantId, startDate))
                .totalQuestions(messageRepository.countUserMessagesByTenantAndDateAfter(tenantId, startDate))
                .build();
    }

    private AnalyticsDto.FeedbackMetrics calculateFeedbackMetrics(String tenantId, Instant startDate) {
        List<MessageFeedback> allFeedback = feedbackRepository.findByTenantIdAndCreatedAtAfter(tenantId, startDate);

        long totalFeedback = allFeedback.size();
        long positiveFeedback = allFeedback.stream()
                .filter(f -> f.getFeedbackType() == FeedbackType.POSITIVE)
                .count();
        long negativeFeedback = totalFeedback - positiveFeedback;

        double positiveRate = totalFeedback > 0 ? (positiveFeedback * 100.0 / totalFeedback) : 0.0;
        double negativeRate = totalFeedback > 0 ? (negativeFeedback * 100.0 / totalFeedback) : 0.0;

        return AnalyticsDto.FeedbackMetrics.builder()
                .totalFeedback(totalFeedback)
                .positiveFeedback(positiveFeedback)
                .negativeFeedback(negativeFeedback)
                .positiveRate(Math.round(positiveRate * 10.0) / 10.0)
                .negativeRate(Math.round(negativeRate * 10.0) / 10.0)
                .build();
    }

    private List<AnalyticsDto.FaqPerformance> calculateTopFaqs(String tenantId, int limit) {
        List<FaqEntry> faqs = faqRepository.findByTenantIdOrderByUsageCountDesc(tenantId);

        return faqs.stream()
                .limit(limit)
                .map(faq -> {
                    // Get messages that used this FAQ
                    Long messageCount = messageRepository.countByFaqEntryIdAndTenantId(faq.getId(), tenantId);

                    // Calculate average confidence for this FAQ
                    Double avgConfidence = messageRepository.averageConfidenceByFaqEntryId(faq.getId());

                    // Get feedback for messages using this FAQ
                    List<MessageFeedback> faqFeedback = feedbackRepository.findByMessageFaqEntryId(faq.getId(), tenantId);

                    long positiveFeedback = faqFeedback.stream()
                            .filter(f -> f.getFeedbackType() == FeedbackType.POSITIVE)
                            .count();
                    long negativeFeedback = faqFeedback.size() - positiveFeedback;

                    double satisfactionRate = faqFeedback.size() > 0
                            ? (positiveFeedback * 100.0 / faqFeedback.size())
                            : 0.0;

                    return AnalyticsDto.FaqPerformance.builder()
                            .faqId(faq.getId())
                            .question(faq.getQuestion())
                            .usageCount(faq.getUsageCount() != null ? faq.getUsageCount() : messageCount)
                            .averageConfidence(avgConfidence != null ? Math.round(avgConfidence * 100.0) / 100.0 : null)
                            .positiveFeedbackCount(positiveFeedback)
                            .negativeFeedbackCount(negativeFeedback)
                            .satisfactionRate(Math.round(satisfactionRate * 10.0) / 10.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private AnalyticsDto.ConversationMetrics calculateConversationMetrics(String tenantId, Instant startDate) {
        long totalConversations = conversationRepository.countByTenantIdAndCreatedAtAfter(tenantId, startDate);
        long activeConversations = conversationRepository.countByTenantIdAndStatus(tenantId, ConversationStatus.ACTIVE);
        long closedConversations = conversationRepository.countByTenantIdAndStatus(tenantId, ConversationStatus.CLOSED);
        long handedOffConversations = conversationRepository.countByTenantIdAndStatus(tenantId, ConversationStatus.HANDED_OFF);

        Long totalMessages = messageRepository.countByTenantIdAndCreatedAtAfter(tenantId, startDate);

        double averageMessagesPerConversation = totalConversations > 0
                ? (totalMessages * 1.0 / totalConversations)
                : 0.0;

        return AnalyticsDto.ConversationMetrics.builder()
                .totalConversations(totalConversations)
                .activeConversations(activeConversations)
                .closedConversations(closedConversations)
                .handedOffConversations(handedOffConversations)
                .averageMessagesPerConversation(Math.round(averageMessagesPerConversation * 10.0) / 10.0)
                .totalMessages(totalMessages)
                .build();
    }

    private Map<LocalDate, Long> calculateQuestionsByDate(String tenantId, Instant startDate) {
        // Get all user messages grouped by date
        List<Object[]> results = messageRepository.countUserMessagesByTenantGroupedByDate(tenantId, startDate);

        Map<LocalDate, Long> questionsByDate = new LinkedHashMap<>();

        // Fill in all dates with 0 if no data
        LocalDate start = LocalDate.ofInstant(startDate, ZoneId.systemDefault());
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            questionsByDate.put(date, 0L);
        }

        // Populate with actual data
        for (Object[] result : results) {
            // Convert java.sql.Date to java.time.LocalDate
            java.sql.Date sqlDate = (java.sql.Date) result[0];
            LocalDate date = sqlDate.toLocalDate();
            Long count = ((Number) result[1]).longValue();
            questionsByDate.put(date, count);
        }

        return questionsByDate;
    }
}
