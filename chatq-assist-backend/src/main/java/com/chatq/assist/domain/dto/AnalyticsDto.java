package com.chatq.assist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {

    // Question/conversation metrics
    private Long totalQuestions;
    private Long answeredQuestions;
    private Long handoffCount;
    private Double deflectionRate; // (answered / total) * 100
    private Double averageConfidence;
    private List<TopQuestion> topQuestions;
    private Map<LocalDate, Long> questionsByDate;

    // Feedback metrics
    private FeedbackMetrics feedbackMetrics;

    // FAQ performance metrics
    private List<FaqPerformance> topFaqs;

    // Conversation metrics
    private ConversationMetrics conversationMetrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopQuestion {
        private String questionHash;
        private Long count;
        private Boolean answered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackMetrics {
        private Long totalFeedback;
        private Long positiveFeedback;
        private Long negativeFeedback;
        private Double positiveRate;
        private Double negativeRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaqPerformance {
        private Long faqId;
        private String question;
        private Long usageCount;
        private Double averageConfidence;
        private Long positiveFeedbackCount;
        private Long negativeFeedbackCount;
        private Double satisfactionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMetrics {
        private Long totalConversations;
        private Long activeConversations;
        private Long closedConversations;
        private Long handedOffConversations;
        private Double averageMessagesPerConversation;
        private Long totalMessages;
    }
}
