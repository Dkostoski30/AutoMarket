package com.automarket.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogRequest(
        @NotBlank @Size(min = 5, max = 100) String title,
        @NotBlank String content,
        String imageUrl
) {}
