package com.automarket.service;

import com.automarket.dto.listing.ListingDetailDto;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.Listing;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingModerationService {

    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public PageResponse<ListingDetailDto> getPendingListings(int page, int size) {
        var results = listingRepository.findPendingApproval(
                PageRequest.of(page, size, Sort.by("createdAt").ascending()));
        return PageResponse.from(results, l -> listingService.toDetailDtoPublic(l));
    }

    @Transactional
    @CacheEvict(value = "listing-detail", allEntries = true)
    public void approve(UUID listingId, String moderatorEmail) {
        Listing listing = getListingOrThrow(listingId);
        listing.setApproved(true);
        listingRepository.save(listing);

        emailService.sendListingApproved(listing);
        log.info("Listing {} approved by {}", listingId, moderatorEmail);
    }

    @Transactional
    public void reject(UUID listingId, String reason, String moderatorEmail) {
        Listing listing = getListingOrThrow(listingId);
        listing.softDelete();
        listingRepository.save(listing);

        emailService.sendListingRejected(listing, reason);
        log.info("Listing {} rejected by {} — reason: {}", listingId, moderatorEmail, reason);
    }

    private Listing getListingOrThrow(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", id));
    }
}
