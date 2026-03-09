package com.automarket.logging;

/**
 * Constants for MDC (Mapped Diagnostic Context) keys used throughout the application.
 * These keys are populated by {@link RequestLoggingFilter} and {@link com.automarket.security.JwtAuthFilter}
 * and appear in every log line, enabling log correlation and tracing.
 */
public final class MdcKeys {

    private MdcKeys() {}

    /** UUID generated per HTTP request — correlates all log lines from a single request */
    public static final String REQUEST_ID = "requestId";

    /** Authenticated user's email, set after JWT validation */
    public static final String USER_ID = "userId";

    /** HTTP method (GET, POST, etc.) */
    public static final String METHOD = "method";

    /** Request URI path */
    public static final String PATH = "path";

    /** Response status code, set after response is committed */
    public static final String STATUS = "status";

    /** Total request duration in milliseconds */
    public static final String DURATION_MS = "durationMs";
}
