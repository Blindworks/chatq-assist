package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.CreateUserRequest;
import com.chatq.assist.domain.dto.UserDto;
import com.chatq.assist.domain.enums.UserRole;
import com.chatq.assist.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /api/users");
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByTenant(
        @PathVariable String tenantId,
        @RequestHeader("X-Tenant-ID") String requestingTenantId,
        Authentication authentication
    ) {
        log.info("GET /api/users/tenant/{}", tenantId);

        // TENANT_ADMIN can only get users from their own tenant
        UserRole role = extractUserRole(authentication);
        if (role == UserRole.TENANT_ADMIN && !requestingTenantId.equals(tenantId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userManagementService.getUsersByTenant(tenantId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return ResponseEntity.ok(userManagementService.getUser(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<UserDto> createUser(
        @RequestBody CreateUserRequest request,
        @RequestHeader("X-Tenant-ID") String requestingTenantId,
        Authentication authentication
    ) {
        log.info("POST /api/users - Creating user: {}", request.getUsername());

        UserRole requestingRole = extractUserRole(authentication);
        UserDto createdUser = userManagementService.createUser(request, requestingTenantId, requestingRole);

        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<UserDto> updateUser(
        @PathVariable Long id,
        @RequestBody CreateUserRequest request,
        @RequestHeader("X-Tenant-ID") String requestingTenantId,
        Authentication authentication
    ) {
        log.info("PUT /api/users/{}", id);

        UserRole requestingRole = extractUserRole(authentication);
        UserDto updatedUser = userManagementService.updateUser(id, request, requestingTenantId, requestingRole);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<UserDto> toggleUserStatus(
        @PathVariable Long id,
        @RequestHeader("X-Tenant-ID") String requestingTenantId,
        Authentication authentication
    ) {
        log.info("POST /api/users/{}/toggle-status", id);

        UserRole requestingRole = extractUserRole(authentication);
        UserDto updatedUser = userManagementService.toggleUserStatus(id, requestingTenantId, requestingRole);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long id,
        @RequestHeader("X-Tenant-ID") String requestingTenantId,
        Authentication authentication
    ) {
        log.warn("DELETE /api/users/{}", id);

        UserRole requestingRole = extractUserRole(authentication);
        userManagementService.deleteUser(id, requestingTenantId, requestingRole);

        return ResponseEntity.noContent().build();
    }

    /**
     * Extract UserRole from Authentication
     */
    private UserRole extractUserRole(Authentication authentication) {
        String roleString = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> auth.startsWith("ROLE_"))
            .map(auth -> auth.substring(5)) // Remove "ROLE_" prefix
            .findFirst()
            .orElse("TENANT_USER");

        return UserRole.valueOf(roleString);
    }
}
