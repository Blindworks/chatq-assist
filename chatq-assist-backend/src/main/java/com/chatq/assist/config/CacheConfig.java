package com.chatq.assist.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * Cache for OpenAI embeddings
     * - Same question = same embedding
     * - TTL: 24 hours (embeddings don't change)
     * - Max size: 10,000 entries
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "embeddings",      // Cache for text embeddings
            "responses",       // Cache for FAQ responses
            "faqMatches"      // Cache for FAQ similarity matches
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .recordStats());

        log.info("Caffeine cache manager initialized with caches: embeddings, responses, faqMatches");
        return cacheManager;
    }
}
