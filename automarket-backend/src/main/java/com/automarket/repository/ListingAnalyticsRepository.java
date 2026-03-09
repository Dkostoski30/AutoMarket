package com.automarket.repository;

import com.automarket.entity.Listing;
import com.automarket.entity.ListingAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingAnalyticsRepository extends JpaRepository<ListingAnalytics, UUID> {
    Optional<ListingAnalytics> findByListingAndDate(Listing listing, LocalDate date);
    List<ListingAnalytics> findByListingOrderByDateDesc(Listing listing);

    @Query("SELECT SUM(a.viewCount) FROM ListingAnalytics a WHERE a.listing = :listing")
    Long sumViewCountByListing(Listing listing);
}
