package com.automarket.service;

import com.automarket.dto.analytics.ListingAnalyticsDto;
import com.automarket.entity.Listing;
import com.automarket.entity.ListingAnalytics;
import com.automarket.repository.ListingAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ListingAnalyticsRepository analyticsRepository;

    /**
     * Asynchronously records a view for a listing.
     * Uses upsert logic: finds or creates today's analytics record, then increments view count.
     */
    @Async
    @Transactional
    public void recordView(Listing listing) {
        try {
            LocalDate today = LocalDate.now();
            ListingAnalytics analytics = analyticsRepository
                    .findByListingAndDate(listing, today)
                    .orElseGet(() -> analyticsRepository.save(
                            ListingAnalytics.builder().listing(listing).date(today).build()));

            analytics.setViewCount(analytics.getViewCount() + 1);
            analyticsRepository.save(analytics);
        } catch (Exception e) {
            log.warn("Failed to record view for listing {}: {}", listing.getId(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ListingAnalyticsDto getAnalytics(UUID listingId, Listing listing) {
        List<ListingAnalytics> records = analyticsRepository.findByListingOrderByDateDesc(listing);

        long totalViews = records.stream().mapToLong(ListingAnalytics::getViewCount).sum();
        long totalInquiries = records.stream().mapToLong(ListingAnalytics::getInquiryCount).sum();
        long totalFavorites = records.stream().mapToLong(ListingAnalytics::getFavoriteCount).sum();

        List<ListingAnalyticsDto.DailyStats> daily = records.stream()
                .limit(30)
                .map(r -> new ListingAnalyticsDto.DailyStats(
                        r.getDate(), r.getViewCount(), r.getInquiryCount(), r.getFavoriteCount()))
                .toList();

        return new ListingAnalyticsDto(listingId, totalViews, totalInquiries, totalFavorites, daily);
    }
}
