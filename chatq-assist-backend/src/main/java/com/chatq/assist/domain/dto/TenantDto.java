package com.chatq.assist.domain.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class TenantDto {
    private Long id;
    private String tenantId;
    private String name;
    private String contactEmail;
    private String domain;
    private String apiKey;
    private Boolean isActive;
    private Integer maxUsers;
    private Integer maxDocuments;
    private String settings;
    private Instant createdAt;
    private Instant updatedAt;
    private Long currentUserCount;
    private Long currentDocumentCount;
}
