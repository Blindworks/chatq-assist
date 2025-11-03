package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.CreateTenantRequest;
import com.chatq.assist.domain.dto.TenantDto;
import com.chatq.assist.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<TenantDto>> getAllTenants() {
        log.info("GET /api/tenants");
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<TenantDto>> getActiveTenants() {
        log.info("GET /api/tenants/active");
        return ResponseEntity.ok(tenantService.getActiveTenants());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<TenantDto> getTenant(@PathVariable Long id) {
        log.info("GET /api/tenants/{}", id);
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @GetMapping("/by-tenant-id/{tenantId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<TenantDto> getTenantByTenantId(@PathVariable String tenantId) {
        log.info("GET /api/tenants/by-tenant-id/{}", tenantId);
        return ResponseEntity.ok(tenantService.getTenantByTenantId(tenantId));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> createTenant(@RequestBody CreateTenantRequest request) {
        log.info("POST /api/tenants - Creating tenant: {}", request.getName());
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> updateTenant(@PathVariable Long id, @RequestBody CreateTenantRequest request) {
        log.info("PUT /api/tenants/{}", id);
        return ResponseEntity.ok(tenantService.updateTenant(id, request));
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> toggleTenantStatus(@PathVariable Long id) {
        log.info("POST /api/tenants/{}/toggle-status", id);
        return ResponseEntity.ok(tenantService.toggleTenantStatus(id));
    }

    @PostMapping("/{id}/regenerate-api-key")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> regenerateApiKey(@PathVariable Long id) {
        log.info("POST /api/tenants/{}/regenerate-api-key", id);
        return ResponseEntity.ok(tenantService.regenerateApiKey(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        log.warn("DELETE /api/tenants/{}", id);
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
