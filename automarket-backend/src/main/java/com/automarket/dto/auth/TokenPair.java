package com.automarket.dto.auth;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInMs
) {}
