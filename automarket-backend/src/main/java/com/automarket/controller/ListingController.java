package com.automarket.controller;

import com.automarket.dto.analytics.ListingAnalyticsDto;
import com.automarket.dto.listing.*;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.Listing;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.ListingRepository;
import com.automarket.service.AnalyticsService;
import com.automarket.service.FavoriteService;
import com.automarket.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@Tag(name = "Listings")
public class ListingController {

    private final ListingService listingService;
    private final FavoriteService favoriteService;
    private final AnalyticsService analyticsService;
    private final ListingRepository listingRepository;

    @GetMapping
    @Operation(summary = "Browse listings with filters and pagination")
    public PageResponse<ListingDto> browse(
            ListingFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return listingService.browse(filter, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get listing details by ID")
    public ListingDetailDto getById(@PathVariable UUID id) {
        return listingService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get listing details by SEO slug")
    public ListingDetailDto getBySlug(@PathVariable String slug) {
        return listingService.getBySlug(slug);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get active featured listings")
    public List<ListingDto> getFeatured() {
        return listingService.getFeatured();
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user's own listings")
    public PageResponse<ListingDto> getMyListings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return listingService.getMyListings(userDetails.getUsername(), page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new listing")
    public ListingDetailDto create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return listingService.create(request, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update an existing listing (owner or admin only)")
    public ListingDetailDto update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return listingService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a listing (owner or admin only)")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        listingService.delete(id, userDetails.getUsername());
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload images to a listing (max 10)")
    public ListingDetailDto addImage(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        return listingService.addImage(id, file, userDetails.getUsername());
    }

    @DeleteMapping("/{listingId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete an image from a listing")
    public void deleteImage(
            @PathVariable UUID listingId,
            @PathVariable UUID imageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        listingService.deleteImage(listingId, imageId, userDetails.getUsername());
    }

    @PostMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add listing to favorites")
    public void favorite(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.add(id, userDetails.getUsername());
    }

    @DeleteMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove listing from favorites")
    public void unfavorite(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.remove(id, userDetails.getUsername());
    }

    @GetMapping("/{id}/analytics")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get analytics for a listing (owner or admin only)")
    public ListingAnalyticsDto getAnalytics(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", id));
        return analyticsService.getAnalytics(id, listing);
    }
}
