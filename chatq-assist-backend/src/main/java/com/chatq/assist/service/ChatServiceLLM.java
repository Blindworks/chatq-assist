package com.chatq.assist.service;

import com.chatq.assist.domain.dto.ChatRequest;
import com.chatq.assist.domain.dto.ChatResponse;
import com.chatq.assist.domain.dto.MessageDto;
import com.chatq.assist.domain.entity.Conversation;
import com.chatq.assist.domain.entity.DocumentChunk;
import com.chatq.assist.domain.entity.FaqEntry;
import com.chatq.assist.domain.entity.Message;
import com.chatq.assist.domain.enums.ConversationStatus;
import com.chatq.assist.domain.enums.MessageRole;
import com.chatq.assist.repository.ConversationRepository;
import com.chatq.assist.repository.DocumentChunkRepository;
import com.chatq.assist.repository.FaqRepository;
import com.chatq.assist.repository.MessageRepository;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceLLM {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final FaqRepository faqRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    private static final double CONFIDENCE_THRESHOLD = 0.75; // Cosine similarity threshold
    private static final int MAX_SIMILAR_FAQS = 3;
    private static final int MAX_SIMILAR_DOCUMENT_CHUNKS = 5;

    @Transactional
    public ChatResponse processChat(ChatRequest request, String tenantId) {
        log.info("Processing chat request with LLM for tenant: {}, question: {}", tenantId, request.getQuestion());

        // Get or create conversation
        Conversation conversation = getOrCreateConversation(request, tenantId);

        // Save user message
        saveMessage(conversation, MessageRole.USER, request.getQuestion(), null, null);

        // Generate embedding for the user's question
        float[] questionEmbedding = embeddingService.generateEmbedding(request.getQuestion());

        // Find similar FAQs using vector similarity
        List<FaqEntry> similarFaqs = findSimilarFaqs(questionEmbedding, tenantId);

        // Find similar document chunks using vector similarity
        List<DocumentChunk> similarDocumentChunks = findSimilarDocumentChunks(questionEmbedding, tenantId);

        String answer;
        List<ChatResponse.SourceReference> sources = new ArrayList<>();
        boolean handoffTriggered = false;
        Double confidenceScore = null;

        if (!similarFaqs.isEmpty() || !similarDocumentChunks.isEmpty()) {
            // We have similar FAQs or documents - use them as context for GPT-4
            FaqEntry bestMatch = similarFaqs.isEmpty() ? null : similarFaqs.get(0);

            // Calculate approximate confidence (inverse of cosine distance would be better, but we'll estimate)
            confidenceScore = 0.8; // High confidence when we have vector matches

            // Build context from similar FAQs and documents
            String context = buildContextFromFaqsAndDocuments(similarFaqs, similarDocumentChunks);

            // Generate answer using GPT-4 with RAG
            answer = generateAnswerWithRAG(request.getQuestion(), context, conversation);

            // Track usage for best matching FAQ
            if (bestMatch != null) {
                bestMatch.setUsageCount(bestMatch.getUsageCount() + 1);
                faqRepository.save(bestMatch);
            }

            // Add source references for FAQs
            for (FaqEntry faq : similarFaqs) {
                sources.add(ChatResponse.SourceReference.builder()
                    .type("FAQ")
                    .title(faq.getQuestion())
                    .id(faq.getId())
                    .build());
            }

            // Add source references for documents
            for (DocumentChunk chunk : similarDocumentChunks) {
                sources.add(ChatResponse.SourceReference.builder()
                    .type("DOCUMENT")
                    .title(chunk.getDocument().getTitle() + " (Chunk " + chunk.getChunkIndex() + ")")
                    .id(chunk.getDocument().getId())
                    .build());
            }

            // Save assistant message
            saveMessage(conversation, MessageRole.ASSISTANT, answer, confidenceScore,
                       bestMatch != null ? bestMatch.getId() : null);

        } else {
            // No similar FAQs found - trigger handoff
            log.warn("No similar FAQs found for question: {}", request.getQuestion());

            answer = "Entschuldigung, ich konnte in unserer Wissensdatenbank keine passende Antwort finden. " +
                     "Ich verbinde Sie gerne mit einem unserer Mitarbeiter, der Ihnen weiterhelfen kann.";

            handoffTriggered = true;
            confidenceScore = 0.0;

            // Update conversation status
            conversation.setStatus(ConversationStatus.HANDED_OFF);
            conversationRepository.save(conversation);

            // Save assistant message
            saveMessage(conversation, MessageRole.ASSISTANT, answer, confidenceScore, null);
        }

        // Update last activity
        conversation.setLastActivityAt(Instant.now());
        conversationRepository.save(conversation);

        log.info("Chat response generated - sessionId: {}, confidence: {}, handoff: {}, sources: {}",
                 conversation.getSessionId(), confidenceScore, handoffTriggered, sources.size());

        return ChatResponse.builder()
            .sessionId(conversation.getSessionId())
            .answer(answer)
            .confidenceScore(confidenceScore)
            .sources(sources)
            .handoffTriggered(handoffTriggered)
            .handoffMessage(handoffTriggered ? "Keine passende Antwort gefunden. Ein Mitarbeiter wird sich bei Ihnen melden." : null)
            .build();
    }

    @Cacheable(value = "faqMatches", key = "#tenantId + '_' + T(java.util.Arrays).hashCode(#embedding)")
    private List<FaqEntry> findSimilarFaqs(float[] embedding, String tenantId) {
        // Convert float[] to PostgreSQL vector format string
        String embeddingString = vectorToString(embedding);
        return faqRepository.findSimilarByEmbedding(tenantId, embeddingString, MAX_SIMILAR_FAQS);
    }

    @Cacheable(value = "documentChunkMatches", key = "#tenantId + '_' + T(java.util.Arrays).hashCode(#embedding)")
    private List<DocumentChunk> findSimilarDocumentChunks(float[] embedding, String tenantId) {
        // Convert float[] to PostgreSQL vector format string
        String embeddingString = vectorToString(embedding);
        return documentChunkRepository.findSimilarCompletedDocumentChunks(tenantId, embeddingString, MAX_SIMILAR_DOCUMENT_CHUNKS);
    }

    private String vectorToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private String buildContextFromFaqsAndDocuments(List<FaqEntry> faqs, List<DocumentChunk> documentChunks) {
        StringBuilder context = new StringBuilder();

        if (!faqs.isEmpty()) {
            context.append("Relevante FAQ-Einträge aus unserer Wissensdatenbank:\n\n");
            for (int i = 0; i < faqs.size(); i++) {
                FaqEntry faq = faqs.get(i);
                context.append(String.format("%d. Frage: %s\n", i + 1, faq.getQuestion()));
                context.append(String.format("   Antwort: %s\n\n", faq.getAnswer()));
            }
        }

        if (!documentChunks.isEmpty()) {
            context.append("Relevante Informationen aus unseren Dokumenten:\n\n");
            for (int i = 0; i < documentChunks.size(); i++) {
                DocumentChunk chunk = documentChunks.get(i);
                context.append(String.format("%d. Aus Dokument '%s':\n", i + 1, chunk.getDocument().getTitle()));
                context.append(String.format("   %s\n\n", chunk.getContent()));
            }
        }

        return context.toString();
    }

    private String buildContextFromFaqs(List<FaqEntry> faqs) {
        StringBuilder context = new StringBuilder();
        context.append("Relevante FAQ-Einträge aus unserer Wissensdatenbank:\n\n");

        for (int i = 0; i < faqs.size(); i++) {
            FaqEntry faq = faqs.get(i);
            context.append(String.format("%d. Frage: %s\n", i + 1, faq.getQuestion()));
            context.append(String.format("   Antwort: %s\n\n", faq.getAnswer()));
        }

        return context.toString();
    }

    private String generateAnswerWithRAG(String question, String context, Conversation conversation) {
        // Get conversation history
        List<Message> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        // Build prompt with system instructions, context, history, and question
        StringBuilder prompt = new StringBuilder();

        prompt.append("Du bist ein hilfreicher Kundenservice-Assistent. ");
        prompt.append("Beantworte Fragen basierend auf den bereitgestellten FAQ-Informationen. ");
        prompt.append("Sei freundlich, professionell und präzise. ");
        prompt.append("Wenn die Informationen nicht ausreichen, sage das ehrlich.\n\n");

        prompt.append(context);
        prompt.append("\n");

        // Add recent conversation history (last 5 messages)
        if (history.size() > 1) { // More than just the current message
            prompt.append("Bisherige Konversation:\n");
            int startIndex = Math.max(0, history.size() - 6);
            for (int i = startIndex; i < history.size() - 1; i++) {
                Message msg = history.get(i);
                prompt.append(String.format("%s: %s\n",
                    msg.getRole() == MessageRole.USER ? "Kunde" : "Assistent",
                    msg.getContent()));
            }
            prompt.append("\n");
        }

        prompt.append("Aktuelle Frage des Kunden: ").append(question).append("\n\n");
        prompt.append("Deine Antwort:");

        log.debug("Sending prompt to GPT-4: {}", prompt.substring(0, Math.min(200, prompt.length())));

        // Call GPT-4
        String response = chatLanguageModel.generate(prompt.toString());

        log.debug("Received response from GPT-4: {}", response.substring(0, Math.min(100, response.length())));

        return response;
    }

    private Conversation getOrCreateConversation(ChatRequest request, String tenantId) {
        String sessionId = request.getSessionId();

        if (sessionId != null && !sessionId.isBlank()) {
            Optional<Conversation> existing = conversationRepository.findBySessionIdAndTenantId(sessionId, tenantId);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setSessionId(sessionId != null && !sessionId.isBlank() ? sessionId : UUID.randomUUID().toString());
        conversation.setTenantId(tenantId);
        conversation.setUserEmail(request.getUserEmail());
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setLastActivityAt(Instant.now());

        return conversationRepository.save(conversation);
    }

    private Message saveMessage(Conversation conversation, MessageRole role, String content, Double confidenceScore, Long faqEntryId) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setConfidenceScore(confidenceScore);
        message.setFaqEntryId(faqEntryId);
        message.setTenantId(conversation.getTenantId());

        return messageRepository.save(message);
    }

    /**
     * Process chat with streaming response via SSE
     */
    @Async
    public void processChatStreaming(ChatRequest request, String tenantId, SseEmitter emitter) {
        try {
            log.info("Processing streaming chat request for tenant: {}, question: {}", tenantId, request.getQuestion());

            // Get or create conversation
            Conversation conversation = getOrCreateConversation(request, tenantId);

            // Save user message
            saveMessage(conversation, MessageRole.USER, request.getQuestion(), null, null);

            // Generate embedding for the user's question
            float[] questionEmbedding = embeddingService.generateEmbedding(request.getQuestion());

            // Find similar FAQs using vector similarity
            List<FaqEntry> similarFaqs = findSimilarFaqs(questionEmbedding, tenantId);

            // Find similar document chunks using vector similarity
            List<DocumentChunk> similarDocumentChunks = findSimilarDocumentChunks(questionEmbedding, tenantId);

            if (similarFaqs.isEmpty() && similarDocumentChunks.isEmpty()) {
                // No FAQs found - send handoff message
                String fallbackMessage = "Entschuldigung, ich konnte in unserer Wissensdatenbank keine passende Antwort finden.";

                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(fallbackMessage));

                emitter.send(SseEmitter.event()
                    .name("metadata")
                    .data(Map.of(
                        "sessionId", conversation.getSessionId(),
                        "handoffTriggered", true,
                        "confidenceScore", 0.0
                    )));

                saveMessage(conversation, MessageRole.ASSISTANT, fallbackMessage, 0.0, null);
                conversation.setStatus(ConversationStatus.HANDED_OFF);
                conversationRepository.save(conversation);

                emitter.complete();
                return;
            }

            // Build context from FAQs and documents
            String context = buildContextFromFaqsAndDocuments(similarFaqs, similarDocumentChunks);
            List<Message> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
            String prompt = buildPrompt(request.getQuestion(), context, history);

            // Track usage for best matching FAQ
            FaqEntry bestMatch = similarFaqs.isEmpty() ? null : similarFaqs.get(0);
            if (bestMatch != null) {
                bestMatch.setUsageCount(bestMatch.getUsageCount() + 1);
                faqRepository.save(bestMatch);
            }

            // Stream response from GPT-4
            StringBuilder fullResponse = new StringBuilder();

            streamingChatLanguageModel.generate(prompt, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    log.debug("Received token from OpenAI: [{}]", token);
                    fullResponse.append(token);
                    try {
                        emitter.send(SseEmitter.event()
                            .name("token")
                            .data(token));
                    } catch (IOException e) {
                        log.error("Error sending SSE token", e);
                    }
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    try {
                        // Send metadata with sources from both FAQs and documents
                        List<Map<String, Object>> sourcesData = new ArrayList<>();

                        // Add FAQ sources
                        similarFaqs.stream()
                            .map(faq -> Map.of(
                                "type", (Object) "FAQ",
                                "title", faq.getQuestion(),
                                "id", faq.getId()
                            ))
                            .forEach(sourcesData::add);

                        // Add document sources
                        similarDocumentChunks.stream()
                            .map(chunk -> Map.of(
                                "type", (Object) "DOCUMENT",
                                "title", chunk.getDocument().getTitle() + " (Chunk " + chunk.getChunkIndex() + ")",
                                "id", chunk.getDocument().getId()
                            ))
                            .forEach(sourcesData::add);

                        emitter.send(SseEmitter.event()
                            .name("metadata")
                            .data(Map.of(
                                "sessionId", conversation.getSessionId(),
                                "confidenceScore", 0.8,
                                "sources", sourcesData,
                                "handoffTriggered", false
                            )));

                        // Save complete message
                        Message savedMessage = saveMessage(conversation, MessageRole.ASSISTANT, fullResponse.toString(), 0.8,
                                                          bestMatch != null ? bestMatch.getId() : null);
                        conversation.setLastActivityAt(Instant.now());
                        conversationRepository.save(conversation);

                        // Send message ID for feedback
                        emitter.send(SseEmitter.event()
                            .name("messageId")
                            .data(Map.of("messageId", savedMessage.getId())));

                        emitter.complete();
                        log.info("Streaming chat completed for session: {}", conversation.getSessionId());
                    } catch (IOException e) {
                        log.error("Error completing SSE stream", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    log.error("Error during streaming chat", error);
                    emitter.completeWithError(error);
                }
            });

        } catch (Exception e) {
            log.error("Error processing streaming chat", e);
            emitter.completeWithError(e);
        }
    }

    private String buildPrompt(String question, String context, List<Message> history) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Du bist ein hilfreicher Kundenservice-Assistent. ");
        prompt.append("Beantworte Fragen basierend auf den bereitgestellten FAQ-Informationen. ");
        prompt.append("Sei freundlich, professionell und präzise. ");
        prompt.append("Wenn die Informationen nicht ausreichen, sage das ehrlich.\n\n");

        prompt.append(context);
        prompt.append("\n");

        // Add recent conversation history (last 5 messages)
        if (history.size() > 1) {
            prompt.append("Bisherige Konversation:\n");
            int startIndex = Math.max(0, history.size() - 6);
            for (int i = startIndex; i < history.size() - 1; i++) {
                Message msg = history.get(i);
                prompt.append(String.format("%s: %s\n",
                    msg.getRole() == MessageRole.USER ? "Kunde" : "Assistent",
                    msg.getContent()));
            }
            prompt.append("\n");
        }

        prompt.append("Aktuelle Frage des Kunden: ").append(question).append("\n\n");
        prompt.append("Deine Antwort:");

        return prompt.toString();
    }

    /**
     * Get conversation history for a session
     */
    public List<MessageDto> getConversationHistory(String sessionId, String tenantId) {
        log.info("Fetching conversation history for sessionId: {}, tenant: {}", sessionId, tenantId);

        Optional<Conversation> conversation = conversationRepository.findBySessionIdAndTenantId(sessionId, tenantId);

        if (conversation.isEmpty()) {
            log.warn("No conversation found for sessionId: {}", sessionId);
            return List.of();
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.get().getId());

        return messages.stream()
            .map(this::toMessageDto)
            .collect(Collectors.toList());
    }

    private MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
            .id(message.getId())
            .role(message.getRole())
            .content(message.getContent())
            .confidenceScore(message.getConfidenceScore())
            .faqEntryId(message.getFaqEntryId())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
