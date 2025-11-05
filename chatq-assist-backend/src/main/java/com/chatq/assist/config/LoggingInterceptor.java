package com.chatq.assist.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Intercepts HTTP requests and responses for logging purposes
 * Adds request ID for tracing and logs request/response details
 */
@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_ATTR = "requestId";
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        request.setAttribute(REQUEST_ID_ATTR, requestId);
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        // Add request ID to response headers for client-side tracking
        response.setHeader(REQUEST_ID_HEADER, requestId);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? uri + "?" + queryString : uri;

        log.info("[{}] {} {} - Started", requestId, method, fullUrl);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);

        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            String logLevel = status >= 500 ? "ERROR" : status >= 400 ? "WARN" : "INFO";

            if ("ERROR".equals(logLevel)) {
                log.error("[{}] {} {} - Completed with status {} in {}ms",
                        requestId, method, uri, status, duration);
            } else if ("WARN".equals(logLevel)) {
                log.warn("[{}] {} {} - Completed with status {} in {}ms",
                        requestId, method, uri, status, duration);
            } else {
                log.info("[{}] {} {} - Completed with status {} in {}ms",
                        requestId, method, uri, status, duration);
            }
        }
    }
}
