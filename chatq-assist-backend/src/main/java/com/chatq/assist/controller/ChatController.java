package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.ChatRequest;
import com.chatq.assist.domain.dto.ChatResponse;
import com.chatq.assist.domain.dto.MessageDto;
import com.chatq.assist.service.ChatServiceLLM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    private final ChatServiceLLM chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Received chat request for tenant: {}, question: {}", tenantId, request.getQuestion());

        ChatResponse response = chatService.processChat(request, tenantId);

        log.info("Chat response - sessionId: {}, confidence: {}, handoff: {}",
                 response.getSessionId(), response.getConfidenceScore(), response.isHandoffTriggered());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Received streaming chat request for tenant: {}, question: {}", tenantId, request.getQuestion());

        SseEmitter emitter = new SseEmitter(60000L); // 60 second timeout

        // Process chat asynchronously and stream response
        chatService.processChatStreaming(request, tenantId, emitter);

        return emitter;
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<MessageDto>> getHistory(
            @PathVariable String sessionId,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Fetching conversation history for sessionId: {}, tenant: {}", sessionId, tenantId);

        List<MessageDto> history = chatService.getConversationHistory(sessionId, tenantId);

        return ResponseEntity.ok(history);
    }
}
