package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.FaqEntryDto;
import com.chatq.assist.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqEntryDto>> getAllFaqs(
        @RequestHeader(value = "X-Tenant-ID", defaultValue = "default-tenant") String tenantId
    ) {
        return ResponseEntity.ok(faqService.getAllFaqs(tenantId));
    }

    @PostMapping
    public ResponseEntity<FaqEntryDto> createFaq(
        @Valid @RequestBody FaqEntryDto dto,
        @RequestHeader(value = "X-Tenant-ID", defaultValue = "default-tenant") String tenantId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(faqService.createFaq(dto, tenantId));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<FaqEntryDto>> createFaqsBatch(
        @Valid @RequestBody List<FaqEntryDto> dtos,
        @RequestHeader(value = "X-Tenant-ID", defaultValue = "default-tenant") String tenantId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(faqService.createFaqsBatch(dtos, tenantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqEntryDto> updateFaq(
        @PathVariable Long id,
        @Valid @RequestBody FaqEntryDto dto,
        @RequestHeader(value = "X-Tenant-ID", defaultValue = "default-tenant") String tenantId
    ) {
        return ResponseEntity.ok(faqService.updateFaq(id, dto, tenantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
