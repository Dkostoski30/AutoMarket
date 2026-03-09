package com.automarket.controller;

import com.automarket.dto.inquiry.*;
import com.automarket.dto.shared.PageResponse;
import com.automarket.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send an inquiry to a seller")
    public InquiryDto send(@Valid @RequestBody SendInquiryRequest request,
                           @AuthenticationPrincipal UserDetails userDetails) {
        return inquiryService.send(request, userDetails.getUsername());
    }

    @GetMapping("/received")
    @Operation(summary = "Get inquiries received on your listings")
    public PageResponse<InquiryDto> getReceived(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return inquiryService.getReceived(userDetails.getUsername(), page, size);
    }

    @GetMapping("/sent")
    @Operation(summary = "Get inquiries you have sent")
    public PageResponse<InquiryDto> getSent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return inquiryService.getSent(userDetails.getUsername(), page, size);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mark an inquiry as read")
    public void markAsRead(@PathVariable UUID id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        inquiryService.markAsRead(id, userDetails.getUsername());
    }
}
