package com.automarket.dto.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ListingAnalyticsDto(
        UUID listingId,
        long totalViews,
        long totalInquiries,
        long totalFavorites,
        List<DailyStats> dailyStats
) {
    public record DailyStats(LocalDate date, int views, int inquiries, int favorites) {}
}
