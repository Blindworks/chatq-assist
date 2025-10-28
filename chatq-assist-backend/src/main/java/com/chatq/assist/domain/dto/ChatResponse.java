package com.chatq.assist.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatResponse {

    private String sessionId;
    private String answer;
    private Double confidenceScore;
    private List<SourceReference> sources;
    private boolean handoffTriggered;
    private String handoffMessage;

    @Data
    @Builder
    public static class SourceReference {
        private String type; // "FAQ" or "DOCUMENT"
        private String title;
        private String url;
        private Long id;
    }
}
