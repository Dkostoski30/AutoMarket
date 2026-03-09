package com.automarket.controller;

import com.automarket.dto.subscription.CheckoutRequest;
import com.automarket.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans")
    public Map<String, Object> getPlans() {
        return Map.of(
                "plans", java.util.List.of(
                        Map.of("id", "FREE", "name", "Free", "maxListings", 3, "price", 0,
                               "features", java.util.List.of("3 active listings", "Basic search", "Inquiries")),
                        Map.of("id", "PREMIUM", "name", "Premium", "maxListings", -1, "price", 1499,
                               "currency", "MKD", "billingPeriod", "monthly",
                               "features", java.util.List.of("Unlimited listings", "Featured listings",
                                       "Analytics dashboard", "Priority support"))
                )
        );
    }

    @PostMapping("/checkout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Start a Stripe checkout session for subscription upgrade")
    public Map<String, String> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String checkoutUrl = subscriptionService.createCheckoutSession(
                userDetails.getUsername(), request.plan(),
                request.successUrl(), request.cancelUrl()
        );
        return Map.of("checkoutUrl", checkoutUrl);
    }
}
