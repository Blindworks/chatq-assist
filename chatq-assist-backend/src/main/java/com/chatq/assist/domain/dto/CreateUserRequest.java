package com.chatq.assist.domain.dto;

import com.chatq.assist.domain.enums.UserRole;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private String tenantId;
    private Boolean enabled;
}
