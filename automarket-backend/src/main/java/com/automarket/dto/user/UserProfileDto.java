package com.automarket.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String name,
        String cityName,
        Instant memberSince,
        int totalListings
) {}
