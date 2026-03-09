package com.automarket.dto.user;

import com.automarket.entity.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String name,
        String phone,
        String cityName,
        User.Plan plan,
        Set<String> roles,
        Instant createdAt
) {}
