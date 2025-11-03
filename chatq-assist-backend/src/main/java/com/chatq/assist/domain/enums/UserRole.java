package com.chatq.assist.domain.enums;

public enum UserRole {
    USER,           // Regular user (legacy, might be deprecated)
    ADMIN,          // Admin user (legacy, might be deprecated)
    SUPER_ADMIN,    // System administrator - can manage all tenants
    TENANT_ADMIN,   // Tenant administrator - can manage their tenant's data and users
    TENANT_USER     // Tenant user - read-only access to their tenant's data
}
