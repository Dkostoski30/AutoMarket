package com.automarket.controller;

import com.automarket.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks")
public class StripeWebhookController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/stripe")
    @ResponseStatus(HttpStatus.OK)
    public void handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        subscriptionService.handleStripeWebhook(payload, signature);
    }
}
