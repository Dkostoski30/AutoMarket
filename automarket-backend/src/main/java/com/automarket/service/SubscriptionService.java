package com.automarket.service;

import com.automarket.entity.Subscription;
import com.automarket.entity.User;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.SubscriptionRepository;
import com.automarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Manages user subscriptions.
 * Stripe integration hooks are marked with TODO for production implementation.
 * The service design is ready for Stripe Checkout Sessions — add the SDK calls in the TODO blocks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Value("${automarket.features.stripe-payments:false}")
    private boolean stripeEnabled;

    /**
     * Creates a Stripe Checkout Session and returns the redirect URL.
     * When stripe-payments feature is disabled, throws a BusinessRuleException.
     */
    @Transactional
    public String createCheckoutSession(String userEmail, String plan, String successUrl, String cancelUrl) {
        if (!stripeEnabled) {
            throw new BusinessRuleException("Payment processing is not enabled on this instance");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));

        // TODO: Create Stripe customer if not exists
        // TODO: Create Stripe Checkout Session with the plan's price ID
        // TODO: Return session URL to redirect user to Stripe

        log.info("Checkout session requested by {} for plan {}", userEmail, plan);
        throw new BusinessRuleException("Stripe not configured. Set STRIPE_SECRET_KEY and configure price IDs.");
    }

    /**
     * Handles Stripe webhook events to update subscription status.
     */
    @Transactional
    public void handleStripeWebhook(String payload, String signature) {
        // TODO: Verify Stripe webhook signature using STRIPE_WEBHOOK_SECRET
        // TODO: Parse event type (customer.subscription.created, updated, deleted)
        // TODO: Update local subscription record accordingly
        log.info("Stripe webhook received");
    }

    @Transactional(readOnly = true)
    public Subscription getSubscription(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription for user: " + userEmail));
    }
}
