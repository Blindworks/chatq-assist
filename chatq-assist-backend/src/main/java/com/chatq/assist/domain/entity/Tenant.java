package com.chatq.assist.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenants_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_tenants_is_active", columnList = "is_active"),
    @Index(name = "idx_tenants_api_key", columnList = "api_key")
})
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private String tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "domain")
    private String domain;

    @Column(name = "api_key", unique = true, length = 500)
    private String apiKey;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "max_users")
    private Integer maxUsers = 10;

    @Column(name = "max_documents")
    private Integer maxDocuments = 100;

    @Column(name = "settings", columnDefinition = "JSONB")
    private String settings; // JSON string for tenant-specific settings

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
