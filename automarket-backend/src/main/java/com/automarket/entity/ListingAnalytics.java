package com.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "listing_analytics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"listing_id", "date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private int viewCount = 0;

    @Column(name = "inquiry_count", nullable = false)
    @Builder.Default
    private int inquiryCount = 0;

    @Column(name = "favorite_count", nullable = false)
    @Builder.Default
    private int favoriteCount = 0;
}
