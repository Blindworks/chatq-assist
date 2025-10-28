package com.chatq.assist.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.model.chat:gpt-4}")
    private String chatModel;

    @Value("${openai.model.embedding:text-embedding-3-small}")
    private String embeddingModel;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        log.info("Initializing OpenAI Chat Model: {}", chatModel);
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(chatModel)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(30))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        log.info("Initializing OpenAI Streaming Chat Model: {}", chatModel);
        return OpenAiStreamingChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(chatModel)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Initializing OpenAI Embedding Model: {}", embeddingModel);
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName(embeddingModel)
                .timeout(Duration.ofSeconds(30))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
