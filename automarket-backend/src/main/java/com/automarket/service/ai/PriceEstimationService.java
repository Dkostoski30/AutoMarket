package com.automarket.service.ai;

import com.automarket.entity.CarDetails;
import java.math.BigDecimal;

/**
 * AI price estimation service interface.
 * Estimates fair market price based on car attributes and city.
 * Enable via {@code automarket.features.price-estimation=true}.
 */
public interface PriceEstimationService {
    PriceEstimate estimate(CarDetails carDetails, String city);

    record PriceEstimate(BigDecimal estimatedPrice, BigDecimal lowerBound, BigDecimal upperBound, String rationale) {}
}
