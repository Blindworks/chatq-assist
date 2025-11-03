package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private Boolean enabled;
    private String tenantId;
    private String tenantName;
    private Instant createdAt;
    private Instant updatedAt;
}
