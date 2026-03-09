package com.automarket.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateProfileRequest(
        @Size(min = 2, max = 100) String name,
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number") String phone,
        UUID cityId
) {}
