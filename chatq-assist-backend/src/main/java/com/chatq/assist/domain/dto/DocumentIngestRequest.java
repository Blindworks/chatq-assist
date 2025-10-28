package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentIngestRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String sourceUrl; // For URL/SITEMAP types

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    // For file uploads, the file will be sent as multipart/form-data
}
