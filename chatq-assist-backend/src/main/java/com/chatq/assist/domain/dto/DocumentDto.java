package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.DocumentStatus;
import com.chatq.assist.domain.enums.DocumentType;
import lombok.Data;

import java.time.Instant;

@Data
public class DocumentDto {

    private Long id;
    private String title;
    private String sourceUrl;
    private DocumentType documentType;
    private DocumentStatus status;
    private Integer chunkCount;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
}
