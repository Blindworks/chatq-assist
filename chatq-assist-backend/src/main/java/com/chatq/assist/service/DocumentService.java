package com.chatq.assist.service;

import com.chatq.assist.domain.dto.DocumentDto;
import com.chatq.assist.domain.entity.Document;
import com.chatq.assist.domain.entity.DocumentChunk;
import com.chatq.assist.domain.enums.DocumentStatus;
import com.chatq.assist.domain.enums.DocumentType;
import com.chatq.assist.repository.DocumentChunkRepository;
import com.chatq.assist.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    @Value("${document.storage.path:./uploads}")
    private String storageBasePath;

    @Value("${document.chunk.size:1000}")
    private int chunkSize; // Characters per chunk

    @Value("${document.chunk.overlap:200}")
    private int chunkOverlap; // Overlapping characters between chunks

    /**
     * Upload and process a document file (PDF, DOCX, TXT)
     */
    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, String title, DocumentType documentType, String tenantId) {
        log.info("Uploading document: {} (type: {}, size: {} bytes) for tenant: {}",
                 title, documentType, file.getSize(), tenantId);

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create document entity
        Document document = new Document();
        document.setTenantId(tenantId);
        document.setTitle(title);
        document.setDocumentType(documentType);
        document.setStatus(DocumentStatus.PENDING);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());

        // Save to get ID
        document = documentRepository.save(document);

        try {
            // Store file on disk
            String filePath = storeFile(file, document.getId(), tenantId);
            document.setFilePath(filePath);
            document = documentRepository.save(document);

            // Process document asynchronously
            processDocumentAsync(document.getId(), tenantId);

            return toDto(document);
        } catch (Exception e) {
            log.error("Failed to upload document: {}", title, e);
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage("Upload failed: " + e.getMessage());
            documentRepository.save(document);
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    /**
     * Ingest document from URL
     */
    @Transactional
    public DocumentDto ingestFromUrl(String url, String title, String tenantId) {
        log.info("Ingesting document from URL: {} for tenant: {}", url, tenantId);

        Document document = new Document();
        document.setTenantId(tenantId);
        document.setTitle(title);
        document.setSourceUrl(url);
        document.setDocumentType(DocumentType.URL);
        document.setStatus(DocumentStatus.PENDING);

        document = documentRepository.save(document);

        // Process URL asynchronously
        processUrlAsync(document.getId(), url, tenantId);

        return toDto(document);
    }

    /**
     * Process document asynchronously
     */
    @Async
    @Transactional
    public void processDocumentAsync(Long documentId, String tenantId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        try {
            log.info("Processing document: {} (type: {})", document.getTitle(), document.getDocumentType());
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);

            // Extract text based on document type
            String fullText = extractText(document);

            // Split into chunks
            List<String> chunks = splitIntoChunks(fullText);
            log.info("Split document into {} chunks", chunks.size());

            // Generate embeddings and save chunks
            int chunkIndex = 0;
            for (String chunkText : chunks) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setDocument(document);
                chunk.setTenantId(tenantId);
                chunk.setChunkIndex(chunkIndex++);
                chunk.setContent(chunkText);
                chunk.setTokenCount(estimateTokenCount(chunkText));

                // Generate embedding
                float[] embedding = embeddingService.generateEmbedding(chunkText);
                chunk.setEmbedding(embedding);

                chunkRepository.save(chunk);
            }

            // Update document status
            document.setChunkCount(chunks.size());
            document.setStatus(DocumentStatus.COMPLETED);
            documentRepository.save(document);

            log.info("Successfully processed document: {} ({} chunks)", document.getTitle(), chunks.size());

        } catch (Exception e) {
            log.error("Failed to process document: {}", document.getTitle(), e);
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage("Processing failed: " + e.getMessage());
            documentRepository.save(document);
        }
    }

    /**
     * Process URL asynchronously
     */
    @Async
    @Transactional
    public void processUrlAsync(Long documentId, String url, String tenantId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        try {
            log.info("Fetching content from URL: {}", url);
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);

            // Fetch and parse HTML
            org.jsoup.nodes.Document jsoupDoc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; ChatQ-Assist/1.0)")
                .timeout(10000)
                .get();

            // Extract text content
            String fullText = jsoupDoc.body().text();
            log.info("Extracted {} characters from URL", fullText.length());

            // Split into chunks
            List<String> chunks = splitIntoChunks(fullText);
            log.info("Split URL content into {} chunks", chunks.size());

            // Generate embeddings and save chunks
            int chunkIndex = 0;
            for (String chunkText : chunks) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setDocument(document);
                chunk.setTenantId(tenantId);
                chunk.setChunkIndex(chunkIndex++);
                chunk.setContent(chunkText);
                chunk.setTokenCount(estimateTokenCount(chunkText));

                // Generate embedding
                float[] embedding = embeddingService.generateEmbedding(chunkText);
                chunk.setEmbedding(embedding);

                chunkRepository.save(chunk);
            }

            // Update document status
            document.setChunkCount(chunks.size());
            document.setStatus(DocumentStatus.COMPLETED);
            documentRepository.save(document);

            log.info("Successfully processed URL: {} ({} chunks)", url, chunks.size());

        } catch (Exception e) {
            log.error("Failed to process URL: {}", url, e);
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage("URL processing failed: " + e.getMessage());
            documentRepository.save(document);
        }
    }

    /**
     * Extract text from document based on type
     */
    private String extractText(Document document) throws IOException {
        String filePath = document.getFilePath();
        if (filePath == null) {
            throw new IllegalStateException("Document file path is null");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Document file not found: " + filePath);
        }

        return switch (document.getDocumentType()) {
            case PDF -> extractTextFromPdf(file);
            case DOCX -> extractTextFromDocx(file);
            case TXT -> extractTextFromTxt(file);
            default -> throw new UnsupportedOperationException(
                "Unsupported document type: " + document.getDocumentType()
            );
        };
    }

    /**
     * Extract text from PDF using Apache PDFBox
     */
    private String extractTextFromPdf(File file) throws IOException {
        log.debug("Extracting text from PDF: {}", file.getName());

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug("Extracted {} characters from PDF", text.length());
            return text;
        }
    }

    /**
     * Extract text from DOCX using Apache POI
     */
    private String extractTextFromDocx(File file) throws IOException {
        log.debug("Extracting text from DOCX: {}", file.getName());

        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }

            String result = text.toString();
            log.debug("Extracted {} characters from DOCX", result.length());
            return result;
        }
    }

    /**
     * Extract text from TXT file
     */
    private String extractTextFromTxt(File file) throws IOException {
        log.debug("Reading text file: {}", file.getName());
        String text = Files.readString(file.toPath());
        log.debug("Read {} characters from TXT", text.length());
        return text;
    }

    /**
     * Split text into overlapping chunks for better context preservation
     */
    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            // Try to break at sentence boundary
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf('.', end);
                int lastQuestion = text.lastIndexOf('?', end);
                int lastExclamation = text.lastIndexOf('!', end);
                int sentenceEnd = Math.max(lastPeriod, Math.max(lastQuestion, lastExclamation));

                if (sentenceEnd > start) {
                    end = sentenceEnd + 1;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            // Move start position with overlap
            start = end - chunkOverlap;
            if (start >= text.length()) {
                break;
            }
        }

        return chunks;
    }

    /**
     * Store uploaded file on disk
     */
    private String storeFile(MultipartFile file, Long documentId, String tenantId) throws IOException {
        // Create directory structure: uploads/{tenantId}/{documentId}/
        Path tenantDir = Paths.get(storageBasePath, tenantId, documentId.toString());
        Files.createDirectories(tenantDir);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Store file
        Path filePath = tenantDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Stored file: {}", filePath);
        return filePath.toString();
    }

    /**
     * Estimate token count (rough approximation: 1 token ≈ 4 characters)
     */
    private int estimateTokenCount(String text) {
        return text.length() / 4;
    }

    /**
     * Get all documents for tenant
     */
    public List<DocumentDto> getAllDocuments(String tenantId) {
        return documentRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    /**
     * Get document by ID
     */
    public DocumentDto getDocument(Long id, String tenantId) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));

        if (!document.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied");
        }

        return toDto(document);
    }

    /**
     * Delete document
     */
    @Transactional
    public void deleteDocument(Long id, String tenantId) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));

        if (!document.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied");
        }

        // Delete file from disk
        if (document.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(document.getFilePath()));
                log.info("Deleted file: {}", document.getFilePath());
            } catch (IOException e) {
                log.warn("Failed to delete file: {}", document.getFilePath(), e);
            }
        }

        // Delete from database (cascades to chunks)
        documentRepository.delete(document);
        log.info("Deleted document: {}", id);
    }

    /**
     * Convert entity to DTO
     */
    private DocumentDto toDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setSourceUrl(document.getSourceUrl());
        dto.setDocumentType(document.getDocumentType());
        dto.setStatus(document.getStatus());
        dto.setChunkCount(document.getChunkCount());
        dto.setErrorMessage(document.getErrorMessage());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }
}
