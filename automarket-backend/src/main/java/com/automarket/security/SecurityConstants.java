package com.automarket.security;

public final class SecurityConstants {

    private SecurityConstants() {}

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/api/v1/listings",
            "/api/v1/listings/{id}",
            "/api/v1/reference/**",
            "/api/v1/blog",
            "/api/v1/blog/{id}",
            "/api/v1/users/{id}",
            "/api/v1/webhooks/**",
            "/api/v1/subscriptions/plans",
            "/actuator/health",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
