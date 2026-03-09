package com.automarket.logging;

import com.automarket.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that:
 * 1. Generates a unique requestId per request and sets it in MDC + response header
 * 2. Enriches MDC with HTTP method and path
 * 3. Logs incoming request and outgoing response with duration
 * 4. Cleans up MDC after the request completes
 */
@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            MDC.put(MdcKeys.REQUEST_ID, requestId);
            MDC.put(MdcKeys.METHOD, request.getMethod());
            MDC.put(MdcKeys.PATH, request.getRequestURI());

            response.setHeader(SecurityConstants.REQUEST_ID_HEADER, requestId);

            log.debug("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(MdcKeys.STATUS, String.valueOf(response.getStatus()));
            MDC.put(MdcKeys.DURATION_MS, String.valueOf(duration));

            log.info("Completed: {} {} -> {} in {}ms",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), duration);

            MDC.clear();
        }
    }
}
