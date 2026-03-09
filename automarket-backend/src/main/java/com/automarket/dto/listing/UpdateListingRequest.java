package com.automarket.dto.listing;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateListingRequest(
        @Size(min = 5, max = 100) String title,
        @Size(min = 20, max = 2000) String description,
        @DecimalMin("0.01") @DecimalMax("99999999.99") BigDecimal price,
        UUID conditionTypeId,
        UUID brandId,
        @Size(max = 100) String model,
        @Min(1886) @Max(2100) Integer registrationYear,
        @Min(0) @Max(9999999) Integer kilometers,
        UUID fuelTypeId,
        UUID bodyTypeId,
        UUID transmissionTypeId,
        @Min(1) @Max(10) Integer numDoors,
        @Min(1) @Max(20) Integer numSeats,
        @Min(1) @Max(2000) Integer kilowatts
) {}
