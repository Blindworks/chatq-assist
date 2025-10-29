package com.chatq.assist.controller;

import com.chatq.assist.domain.dto.AnalyticsDto;
import com.chatq.assist.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AnalyticsController {

    private static final String DEFAULT_TENANT_ID = "default-tenant";
    private final AnalyticsService analyticsService;

    /**
     * Get analytics data for the tenant
     * @param tenantId Tenant ID from header (defaults to "default-tenant")
     * @param daysBack Number of days to look back (defaults to 30)
     * @return Analytics data including feedback, FAQ performance, and conversation metrics
     */
    @GetMapping
    public ResponseEntity<AnalyticsDto> getAnalytics(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestParam(value = "daysBack", required = false, defaultValue = "30") Integer daysBack
    ) {
        log.info("Fetching analytics for tenant: {}, daysBack: {}", tenantId, daysBack);
        AnalyticsDto analytics = analyticsService.getAnalytics(tenantId, daysBack);
        return ResponseEntity.ok(analytics);
    }
}
