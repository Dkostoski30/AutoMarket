package com.automarket.dto.listing;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateListingRequest(
        @NotBlank @Size(min = 5, max = 100) String title,
        @NotBlank @Size(min = 20, max = 2000) String description,
        @NotNull @DecimalMin("0.01") @DecimalMax("99999999.99") BigDecimal price,
        @NotNull UUID conditionTypeId,
        @NotNull UUID brandId,
        @NotBlank @Size(max = 100) String model,
        @NotNull @Min(1886) @Max(2100) Integer registrationYear,
        @NotNull @Min(0) @Max(9999999) Integer kilometers,
        @NotNull UUID fuelTypeId,
        @NotNull UUID bodyTypeId,
        @NotNull UUID transmissionTypeId,
        @Min(1) @Max(10) Integer numDoors,
        @Min(1) @Max(20) Integer numSeats,
        @Min(1) @Max(2000) Integer kilowatts
) {}
