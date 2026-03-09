package com.automarket.dto.listing;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Full listing detail DTO including images and complete seller info */
public record ListingDetailDto(
        UUID id,
        String slug,
        String title,
        String description,
        BigDecimal price,
        boolean featured,
        boolean approved,
        Instant createdAt,
        List<ImageDto> images,
        CarDetailsFullDto carDetails,
        SellerDetailDto seller,
        ConditionDto condition
) {
    public record ImageDto(UUID id, String url, int displayOrder) {}

    public record CarDetailsFullDto(
            UUID brandId,
            String brandName,
            String model,
            Integer registrationYear,
            Integer kilometers,
            UUID fuelTypeId,
            String fuelTypeName,
            UUID bodyTypeId,
            String bodyTypeName,
            UUID transmissionTypeId,
            String transmissionTypeName,
            Integer numDoors,
            Integer numSeats,
            Integer kilowatts
    ) {}

    public record SellerDetailDto(
            UUID id,
            String name,
            String phone,
            String cityName,
            Instant memberSince
    ) {}

    public record ConditionDto(UUID id, String name) {}
}
