package com.chatq.assist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response format for all API errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error type/code (e.g., "VALIDATION_ERROR", "RESOURCE_NOT_FOUND")
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path where error occurred
     */
    private String path;

    /**
     * Timestamp when error occurred
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Optional field-level validation errors
     */
    private List<FieldError> fieldErrors;

    /**
     * Represents a field-level validation error
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
