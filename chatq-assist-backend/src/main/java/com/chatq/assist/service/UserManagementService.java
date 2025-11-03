package com.chatq.assist.service;

import com.chatq.assist.domain.dto.CreateUserRequest;
import com.chatq.assist.domain.dto.UserDto;
import com.chatq.assist.domain.entity.Tenant;
import com.chatq.assist.domain.entity.User;
import com.chatq.assist.domain.enums.UserRole;
import com.chatq.assist.repository.TenantRepository;
import com.chatq.assist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users (SUPER_ADMIN only)
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    /**
     * Get users by tenant ID
     */
    public List<UserDto> getUsersByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    /**
     * Get user by ID
     */
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toDto(user);
    }

    /**
     * Create a new user
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request, String requestingUserTenantId, UserRole requestingUserRole) {
        log.info("Creating user: {} for tenant: {}", request.getUsername(), request.getTenantId());

        // Validate username doesn't already exist
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Validate tenant exists
        Tenant tenant = tenantRepository.findByTenantId(request.getTenantId())
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + request.getTenantId()));

        // Authorization checks
        validateUserCreationPermissions(request, requestingUserTenantId, requestingUserRole);

        // Check tenant user limit
        long currentUserCount = userRepository.countByTenantId(request.getTenantId());
        if (currentUserCount >= tenant.getMaxUsers()) {
            throw new RuntimeException("Tenant has reached maximum user limit: " + tenant.getMaxUsers());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.TENANT_USER);
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        user.setTenantId(request.getTenantId());
        user.setTenant(tenant);

        user = userRepository.save(user);
        log.info("Created user: {} with role: {}", user.getUsername(), user.getRole());

        return toDto(user);
    }

    /**
     * Update user
     */
    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest request, String requestingUserTenantId, UserRole requestingUserRole) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // Authorization check
        validateUserUpdatePermissions(user, requestingUserTenantId, requestingUserRole);

        // Update fields
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            // Only SUPER_ADMIN can change roles
            if (requestingUserRole == UserRole.SUPER_ADMIN) {
                user.setRole(request.getRole());
            } else {
                log.warn("User {} attempted to change role without SUPER_ADMIN permissions", requestingUserTenantId);
            }
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        user = userRepository.save(user);
        log.info("Updated user: {}", user.getUsername());

        return toDto(user);
    }

    /**
     * Delete user
     */
    @Transactional
    public void deleteUser(Long id, String requestingUserTenantId, UserRole requestingUserRole) {
        log.warn("Deleting user: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // Prevent deletion of the default admin
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("Cannot delete default admin user");
        }

        // Authorization check
        validateUserUpdatePermissions(user, requestingUserTenantId, requestingUserRole);

        userRepository.delete(user);
        log.warn("Deleted user: {}", user.getUsername());
    }

    /**
     * Toggle user enabled status
     */
    @Transactional
    public UserDto toggleUserStatus(Long id, String requestingUserTenantId, UserRole requestingUserRole) {
        log.info("Toggling user status: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // Authorization check
        validateUserUpdatePermissions(user, requestingUserTenantId, requestingUserRole);

        user.setEnabled(!user.getEnabled());
        user = userRepository.save(user);

        log.info("User {} is now {}", user.getUsername(), user.getEnabled() ? "enabled" : "disabled");
        return toDto(user);
    }

    /**
     * Validate user creation permissions based on RBAC
     */
    private void validateUserCreationPermissions(CreateUserRequest request, String requestingUserTenantId, UserRole requestingUserRole) {
        switch (requestingUserRole) {
            case SUPER_ADMIN:
                // Can create any user for any tenant
                break;

            case TENANT_ADMIN:
                // Can only create users for their own tenant
                if (!requestingUserTenantId.equals(request.getTenantId())) {
                    throw new RuntimeException("TENANT_ADMIN can only create users for their own tenant");
                }
                // Cannot create SUPER_ADMIN users
                if (request.getRole() == UserRole.SUPER_ADMIN) {
                    throw new RuntimeException("TENANT_ADMIN cannot create SUPER_ADMIN users");
                }
                break;

            default:
                throw new RuntimeException("Insufficient permissions to create users");
        }
    }

    /**
     * Validate user update/delete permissions based on RBAC
     */
    private void validateUserUpdatePermissions(User targetUser, String requestingUserTenantId, UserRole requestingUserRole) {
        switch (requestingUserRole) {
            case SUPER_ADMIN:
                // Can update/delete any user
                break;

            case TENANT_ADMIN:
                // Can only update/delete users from their own tenant
                if (!requestingUserTenantId.equals(targetUser.getTenantId())) {
                    throw new RuntimeException("TENANT_ADMIN can only manage users from their own tenant");
                }
                // Cannot modify SUPER_ADMIN users
                if (targetUser.getRole() == UserRole.SUPER_ADMIN) {
                    throw new RuntimeException("TENANT_ADMIN cannot modify SUPER_ADMIN users");
                }
                break;

            default:
                throw new RuntimeException("Insufficient permissions to modify users");
        }
    }

    /**
     * Convert entity to DTO
     */
    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setTenantId(user.getTenantId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Add tenant name if available
        if (user.getTenant() != null) {
            dto.setTenantName(user.getTenant().getName());
        }

        return dto;
    }
}
