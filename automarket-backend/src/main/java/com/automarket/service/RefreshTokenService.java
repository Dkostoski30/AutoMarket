package com.automarket.service;

import com.automarket.entity.RefreshToken;
import com.automarket.entity.User;
import com.automarket.exception.BusinessRuleException;
import com.automarket.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${automarket.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiryMs))
                .build();
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken validateAndRotate(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessRuleException("Invalid refresh token"));

        if (!token.isValid()) {
            // Possible token reuse — revoke all user tokens
            refreshTokenRepository.revokeAllUserTokens(token.getUser());
            throw new BusinessRuleException("Refresh token is expired or revoked");
        }

        // Rotate: revoke old, issue new
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        return createRefreshToken(token.getUser());
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Scheduled(cron = "0 0 3 * * *") // 3 AM daily
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens();
    }
}
