package com.chatq.assist.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsDto {

    private Long totalQuestions;
    private Long answeredQuestions;
    private Long handoffCount;
    private Double deflectionRate; // (answered / total) * 100
    private Double averageConfidence;
    private List<TopQuestion> topQuestions;
    private Map<LocalDate, Long> questionsByDate;

    @Data
    @Builder
    public static class TopQuestion {
        private String questionHash;
        private Long count;
        private Boolean answered;
    }
}
