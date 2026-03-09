package com.automarket.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SendInquiryRequest(
        @NotNull UUID listingId,
        @NotBlank @Size(min = 10, max = 2000) String message
) {}
