package com.automarket.dto.listing;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Filter criteria for browsing listings. All fields are optional.
 * Handled by {@link com.automarket.specification.ListingSpecification}.
 */
public record ListingFilterRequest(
        String search,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Integer yearFrom,
        Integer yearTo,
        Integer kilometersFrom,
        Integer kilometersTo,
        Integer kilowattsFrom,
        Integer kilowattsTo,
        List<UUID> fuelTypeIds,
        List<UUID> bodyTypeIds,
        List<UUID> conditionTypeIds,
        List<UUID> transmissionTypeIds,
        List<UUID> brandIds,
        UUID cityId,
        Boolean featured,
        String sortBy,       // price, year, createdAt
        String sortDir       // asc, desc
) {}
