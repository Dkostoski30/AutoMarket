package com.automarket.service;

import com.automarket.dto.listing.ListingDto;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.Favorite;
import com.automarket.entity.Listing;
import com.automarket.entity.User;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.FavoriteRepository;
import com.automarket.repository.ListingRepository;
import com.automarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingService listingService;

    @Transactional
    public void add(UUID listingId, String userEmail) {
        User user = getUserOrThrow(userEmail);
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", listingId));

        if (favoriteRepository.existsByUserAndListing(user, listing)) {
            throw new BusinessRuleException("Listing already in favorites");
        }

        favoriteRepository.save(Favorite.builder().user(user).listing(listing).build());
        log.debug("Favorite added: {} -> {}", userEmail, listingId);
    }

    @Transactional
    public void remove(UUID listingId, String userEmail) {
        User user = getUserOrThrow(userEmail);
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", listingId));

        if (!favoriteRepository.existsByUserAndListing(user, listing)) {
            throw new BusinessRuleException("Listing not in favorites");
        }

        favoriteRepository.deleteByUserAndListing(user, listing);
        log.debug("Favorite removed: {} -> {}", userEmail, listingId);
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingDto> getFavorites(String userEmail, int page, int size) {
        User user = getUserOrThrow(userEmail);
        return PageResponse.from(
                favoriteRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size)),
                fav -> listingService.toDetailDtoPublic(fav.getListing()) != null
                        ? listingService.toListingDtoPublic(fav.getListing())
                        : null
        );
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }
}
