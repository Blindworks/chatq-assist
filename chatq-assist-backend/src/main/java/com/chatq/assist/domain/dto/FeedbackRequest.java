package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotNull(message = "Message ID is required")
    private Long messageId;

    @NotNull(message = "Feedback type is required")
    private FeedbackType feedbackType;

    private String comment;
}
