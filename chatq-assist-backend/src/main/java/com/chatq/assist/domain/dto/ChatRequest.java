package com.chatq.assist.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "Question is required")
    private String question;

    private String sessionId; // Optional, generated if not provided

    private String userEmail; // Optional, for handoff
}
