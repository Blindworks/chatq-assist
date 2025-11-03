package com.chatq.assist.service;

import com.chatq.assist.domain.dto.CreateTenantRequest;
import com.chatq.assist.domain.dto.TenantDto;
import com.chatq.assist.domain.entity.Tenant;
import com.chatq.assist.repository.DocumentRepository;
import com.chatq.assist.repository.TenantRepository;
import com.chatq.assist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    /**
     * Get all tenants
     */
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toDto)
            .toList();
    }

    /**
     * Get active tenants only
     */
    public List<TenantDto> getActiveTenants() {
        return tenantRepository.findByIsActiveOrderByCreatedAtDesc(true)
            .stream()
            .map(this::toDto)
            .toList();
    }

    /**
     * Get tenant by ID
     */
    public TenantDto getTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));
        return toDto(tenant);
    }

    /**
     * Get tenant by tenant ID string
     */
    public TenantDto getTenantByTenantId(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
        return toDto(tenant);
    }

    /**
     * Create a new tenant
     */
    @Transactional
    public TenantDto createTenant(CreateTenantRequest request) {
        log.info("Creating tenant: {}", request.getName());

        // Validate tenant ID doesn't already exist
        String tenantId = request.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            // Auto-generate tenant ID from name
            tenantId = generateTenantId(request.getName());
        }

        if (tenantRepository.existsByTenantId(tenantId)) {
            throw new RuntimeException("Tenant ID already exists: " + tenantId);
        }

        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        tenant.setName(request.getName());
        tenant.setContactEmail(request.getContactEmail());
        tenant.setDomain(request.getDomain());
        tenant.setIsActive(true);
        tenant.setMaxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 10);
        tenant.setMaxDocuments(request.getMaxDocuments() != null ? request.getMaxDocuments() : 100);
        tenant.setSettings(request.getSettings());

        // Generate API key
        tenant.setApiKey(generateApiKey());

        tenant = tenantRepository.save(tenant);
        log.info("Created tenant: {} with ID: {}", tenant.getName(), tenant.getTenantId());

        return toDto(tenant);
    }

    /**
     * Update tenant
     */
    @Transactional
    public TenantDto updateTenant(Long id, CreateTenantRequest request) {
        log.info("Updating tenant: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));

        tenant.setName(request.getName());
        tenant.setContactEmail(request.getContactEmail());
        tenant.setDomain(request.getDomain());
        tenant.setMaxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : tenant.getMaxUsers());
        tenant.setMaxDocuments(request.getMaxDocuments() != null ? request.getMaxDocuments() : tenant.getMaxDocuments());

        if (request.getSettings() != null) {
            tenant.setSettings(request.getSettings());
        }

        tenant = tenantRepository.save(tenant);
        log.info("Updated tenant: {}", tenant.getTenantId());

        return toDto(tenant);
    }

    /**
     * Toggle tenant active status
     */
    @Transactional
    public TenantDto toggleTenantStatus(Long id) {
        log.info("Toggling tenant status: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));

        tenant.setIsActive(!tenant.getIsActive());
        tenant = tenantRepository.save(tenant);

        log.info("Tenant {} is now {}", tenant.getTenantId(), tenant.getIsActive() ? "active" : "inactive");
        return toDto(tenant);
    }

    /**
     * Regenerate API key for tenant
     */
    @Transactional
    public TenantDto regenerateApiKey(Long id) {
        log.info("Regenerating API key for tenant: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));

        tenant.setApiKey(generateApiKey());
        tenant = tenantRepository.save(tenant);

        log.info("Regenerated API key for tenant: {}", tenant.getTenantId());
        return toDto(tenant);
    }

    /**
     * Delete tenant (careful - cascades to users!)
     */
    @Transactional
    public void deleteTenant(Long id) {
        log.warn("Deleting tenant: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));

        // Prevent deletion of system tenant
        if ("system".equals(tenant.getTenantId())) {
            throw new RuntimeException("Cannot delete system tenant");
        }

        tenantRepository.delete(tenant);
        log.warn("Deleted tenant: {}", tenant.getTenantId());
    }

    /**
     * Generate a unique tenant ID from name
     */
    private String generateTenantId(String name) {
        String base = name.toLowerCase()
            .replaceAll("[^a-z0-9]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");

        String tenantId = base;
        int counter = 1;

        while (tenantRepository.existsByTenantId(tenantId)) {
            tenantId = base + "-" + counter++;
        }

        return tenantId;
    }

    /**
     * Generate a secure API key
     */
    private String generateApiKey() {
        return "cqa_" + UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Convert entity to DTO with usage counts
     */
    private TenantDto toDto(Tenant tenant) {
        TenantDto dto = new TenantDto();
        dto.setId(tenant.getId());
        dto.setTenantId(tenant.getTenantId());
        dto.setName(tenant.getName());
        dto.setContactEmail(tenant.getContactEmail());
        dto.setDomain(tenant.getDomain());
        dto.setApiKey(tenant.getApiKey());
        dto.setIsActive(tenant.getIsActive());
        dto.setMaxUsers(tenant.getMaxUsers());
        dto.setMaxDocuments(tenant.getMaxDocuments());
        dto.setSettings(tenant.getSettings());
        dto.setCreatedAt(tenant.getCreatedAt());
        dto.setUpdatedAt(tenant.getUpdatedAt());

        // Add current usage counts
        dto.setCurrentUserCount(userRepository.countByTenantId(tenant.getTenantId()));
        dto.setCurrentDocumentCount(documentRepository.countByTenantId(tenant.getTenantId()));

        return dto;
    }
}
