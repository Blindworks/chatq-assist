package com.chatq.assist.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class FaqEntryDto {

    private Long id;

    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    private Set<String> tags;
    private Boolean isActive;
    private Integer displayOrder;
    private Long usageCount;
    private Instant createdAt;
    private Instant updatedAt;
}
