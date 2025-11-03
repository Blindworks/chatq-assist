package com.chatq.assist.domain.dto;

import lombok.Data;

@Data
public class CreateTenantRequest {
    private String tenantId; // Optional - will be auto-generated if not provided
    private String name;
    private String contactEmail;
    private String domain;
    private Integer maxUsers;
    private Integer maxDocuments;
    private String settings; // JSON string
}
