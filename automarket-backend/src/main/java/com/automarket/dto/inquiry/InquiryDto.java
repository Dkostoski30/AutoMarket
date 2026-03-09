package com.automarket.dto.inquiry;

import java.time.Instant;
import java.util.UUID;

public record InquiryDto(
        UUID id,
        UUID listingId,
        String listingTitle,
        UUID senderId,
        String senderName,
        String message,
        boolean readBySeller,
        Instant createdAt
) {}
