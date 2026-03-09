package com.automarket.repository;

import com.automarket.entity.Listing;
import com.automarket.entity.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingImageRepository extends JpaRepository<ListingImage, UUID> {

    List<ListingImage> findByListingOrderByDisplayOrderAsc(Listing listing);

    int countByListing(Listing listing);
}
