package com.chatq.assist.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    /**
     * Generate embedding vector for given text using OpenAI
     */
    @Cacheable("embeddings")
    public float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            log.warn("Attempted to generate embedding for empty text");
            return new float[0];
        }

        try {
            log.debug("Generating embedding for text: {}", text.substring(0, Math.min(50, text.length())));
            Response<Embedding> response = embeddingModel.embed(text);
            Embedding embedding = response.content();

            return embedding.vector();
        } catch (Exception e) {
            log.error("Failed to generate embedding for text: {}", text, e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }

    /**
     * Generate embedding for FAQ entry (question + answer + tags)
     */
    @Cacheable("embeddings")
    public float[] generateFaqEmbedding(String question, String answer, java.util.Set<String> tags) {
        // Combine question, answer, and tags for richer semantic representation
        StringBuilder textToEmbed = new StringBuilder();
        textToEmbed.append("Question: ").append(question).append("\n");
        textToEmbed.append("Answer: ").append(answer);

        if (tags != null && !tags.isEmpty()) {
            textToEmbed.append("\nTags: ").append(String.join(", ", tags));
        }

        return generateEmbedding(textToEmbed.toString());
    }
}
