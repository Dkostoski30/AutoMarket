package com.automarket.dto.listing;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Summary DTO used in listing list/search results */
public record ListingDto(
        UUID id,
        String slug,
        String title,
        BigDecimal price,
        boolean featured,
        boolean approved,
        Instant createdAt,
        String thumbnailUrl,
        CarDetailsDto carDetails,
        SellerSummaryDto seller
) {
    public record CarDetailsDto(
            String brandName,
            String model,
            Integer registrationYear,
            Integer kilometers,
            String fuelTypeName,
            String bodyTypeName,
            String transmissionTypeName,
            Integer kilowatts
    ) {}

    public record SellerSummaryDto(
            UUID id,
            String name,
            String cityName
    ) {}
}
