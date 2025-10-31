package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.DocumentDto;
import com.chatq.assist.domain.dto.DocumentIngestRequest;
import com.chatq.assist.domain.enums.DocumentType;
import com.chatq.assist.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    private final DocumentService documentService;

    /**
     * Upload a document file (PDF, DOCX, TXT)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("documentType") DocumentType documentType,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Received document upload request: title={}, type={}, size={} bytes, tenant={}",
                 title, documentType, file.getSize(), tenantId);

        // Validate file type
        if (documentType == DocumentType.URL || documentType == DocumentType.SITEMAP) {
            return ResponseEntity.badRequest().build();
        }

        DocumentDto document = documentService.uploadDocument(file, title, documentType, tenantId);

        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /**
     * Ingest document from URL
     */
    @PostMapping("/ingest")
    public ResponseEntity<DocumentDto> ingestDocument(
            @Valid @RequestBody DocumentIngestRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Received document ingest request: title={}, url={}, type={}, tenant={}",
                 request.getTitle(), request.getSourceUrl(), request.getDocumentType(), tenantId);

        // Validate that URL is provided for URL/SITEMAP types
        if ((request.getDocumentType() == DocumentType.URL || request.getDocumentType() == DocumentType.SITEMAP)
            && (request.getSourceUrl() == null || request.getSourceUrl().isBlank())) {
            return ResponseEntity.badRequest().build();
        }

        DocumentDto document = documentService.ingestFromUrl(
            request.getSourceUrl(),
            request.getTitle(),
            tenantId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /**
     * Get all documents for tenant
     */
    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments(
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Fetching all documents for tenant: {}", tenantId);

        List<DocumentDto> documents = documentService.getAllDocuments(tenantId);

        return ResponseEntity.ok(documents);
    }

    /**
     * Get document by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Fetching document: id={}, tenant={}", id, tenantId);

        DocumentDto document = documentService.getDocument(id, tenantId);

        return ResponseEntity.ok(document);
    }

    /**
     * Delete document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-ID", required = false, defaultValue = DEFAULT_TENANT_ID) String tenantId) {

        log.info("Deleting document: id={}, tenant={}", id, tenantId);

        documentService.deleteDocument(id, tenantId);

        return ResponseEntity.noContent().build();
    }
}
