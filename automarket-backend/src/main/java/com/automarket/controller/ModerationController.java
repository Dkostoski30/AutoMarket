package com.automarket.controller;

import com.automarket.dto.listing.ListingDetailDto;
import com.automarket.dto.shared.PageResponse;
import com.automarket.service.ListingModerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@Tag(name = "Moderation")
public class ModerationController {

    private final ListingModerationService moderationService;

    @GetMapping("/listings")
    @Operation(summary = "Get all listings pending approval")
    public PageResponse<ListingDetailDto> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return moderationService.getPendingListings(page, size);
    }

    @PostMapping("/listings/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Approve a listing")
    public void approve(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        moderationService.approve(id, userDetails.getUsername());
    }

    @PostMapping("/listings/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reject a listing with optional reason")
    public void reject(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String reason = body != null ? body.get("reason") : null;
        moderationService.reject(id, reason, userDetails.getUsername());
    }
}
