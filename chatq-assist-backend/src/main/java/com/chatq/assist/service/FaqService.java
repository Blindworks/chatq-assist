package com.chatq.assist.service;

import com.chatq.assist.domain.dto.FaqEntryDto;
import com.chatq.assist.domain.entity.FaqEntry;
import com.chatq.assist.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService {

    private final FaqRepository faqRepository;
    private final EmbeddingService embeddingService;

    public List<FaqEntryDto> getAllFaqs(String tenantId) {
        return faqRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public FaqEntryDto createFaq(FaqEntryDto dto, String tenantId) {
        FaqEntry entity = new FaqEntry();
        entity.setQuestion(dto.getQuestion());
        entity.setAnswer(dto.getAnswer());
        entity.setTags(dto.getTags());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setTenantId(tenantId);
        entity.setUsageCount(0L);

        // Generate embedding using OpenAI
        float[] embedding = embeddingService.generateFaqEmbedding(
            dto.getQuestion(),
            dto.getAnswer(),
            dto.getTags()
        );
        entity.setEmbedding(embedding);

        return toDto(faqRepository.save(entity));
    }

    @Transactional
    public FaqEntryDto updateFaq(Long id, FaqEntryDto dto, String tenantId) {
        FaqEntry entity = faqRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FAQ not found"));

        entity.setQuestion(dto.getQuestion());
        entity.setAnswer(dto.getAnswer());
        entity.setTags(dto.getTags());
        entity.setIsActive(dto.getIsActive());
        entity.setDisplayOrder(dto.getDisplayOrder());

        // Regenerate embedding when FAQ is updated
        float[] embedding = embeddingService.generateFaqEmbedding(
            dto.getQuestion(),
            dto.getAnswer(),
            dto.getTags()
        );
        entity.setEmbedding(embedding);

        return toDto(faqRepository.save(entity));
    }

    @Transactional
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    /**
     * Batch create FAQs with optimized embedding generation.
     * This is more efficient than creating FAQs one by one because:
     * 1. Single database transaction
     * 2. Embeddings are generated in batch (can be parallelized in future)
     */
    @Transactional
    public List<FaqEntryDto> createFaqsBatch(List<FaqEntryDto> dtos, String tenantId) {
        log.info("Batch creating {} FAQs for tenant: {}", dtos.size(), tenantId);

        List<FaqEntry> entities = new ArrayList<>();

        for (FaqEntryDto dto : dtos) {
            FaqEntry entity = new FaqEntry();
            entity.setQuestion(dto.getQuestion());
            entity.setAnswer(dto.getAnswer());
            entity.setTags(dto.getTags());
            entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
            entity.setDisplayOrder(dto.getDisplayOrder());
            entity.setTenantId(tenantId);
            entity.setUsageCount(0L);

            // Generate embedding (cached if already generated for same text)
            float[] embedding = embeddingService.generateFaqEmbedding(
                dto.getQuestion(),
                dto.getAnswer(),
                dto.getTags()
            );
            entity.setEmbedding(embedding);

            entities.add(entity);
        }

        // Batch save all FAQs in single transaction
        List<FaqEntry> savedEntities = faqRepository.saveAll(entities);

        log.info("Successfully created {} FAQs in batch", savedEntities.size());

        return savedEntities.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private FaqEntryDto toDto(FaqEntry entity) {
        FaqEntryDto dto = new FaqEntryDto();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setAnswer(entity.getAnswer());
        dto.setTags(entity.getTags());
        dto.setIsActive(entity.getIsActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setUsageCount(entity.getUsageCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
