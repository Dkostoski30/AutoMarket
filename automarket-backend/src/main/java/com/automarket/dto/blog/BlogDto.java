package com.automarket.dto.blog;

import java.time.Instant;
import java.util.UUID;

public record BlogDto(
        UUID id,
        String title,
        String content,
        String imageUrl,
        String authorName,
        Instant createdAt
) {}
