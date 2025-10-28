package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private MessageRole role;
    private String content;
    private Double confidenceScore;
    private Long faqEntryId;
    private Instant createdAt;
}
