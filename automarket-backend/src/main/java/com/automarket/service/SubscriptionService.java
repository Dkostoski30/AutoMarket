package com.automarket.service;

import com.automarket.entity.Subscription;
import com.automarket.entity.User;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.SubscriptionRepository;
import com.automarket.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Manages user subscriptions via Stripe Checkout Sessions.
 *
 * <p>Required environment variables when stripe-payments is enabled:
 * <ul>
 *   <li>STRIPE_SECRET_KEY — Stripe API secret key (sk_live_... or sk_test_...)</li>
 *   <li>STRIPE_WEBHOOK_SECRET — Stripe endpoint signing secret (whsec_...)</li>
 *   <li>STRIPE_PRICE_PREMIUM — Stripe Price ID for the PREMIUM plan (price_...)</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Value("${automarket.features.stripe-payments:false}")
    private boolean stripeEnabled;

    @Value("${automarket.stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${automarket.stripe.webhook-secret:}")
    private String stripeWebhookSecret;

    @Value("${automarket.stripe.price-id-premium:}")
    private String premiumPriceId;

    @PostConstruct
    void init() {
        if (stripeEnabled && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
            log.info("Stripe initialized — payments enabled");
        }
    }

    /**
     * Creates a Stripe Checkout Session and returns the redirect URL.
     * Throws BusinessRuleException when Stripe is disabled or not configured.
     */
    @Transactional
    public String createCheckoutSession(String userEmail, String plan, String successUrl, String cancelUrl) {
        if (!stripeEnabled) {
            throw new BusinessRuleException("Payment processing is not enabled on this instance");
        }
        if (stripeSecretKey.isBlank()) {
            throw new BusinessRuleException("Stripe is not configured. Contact the administrator.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));

        String priceId = resolvePriceId(plan);

        try {
            String customerId = resolveOrCreateStripeCustomer(user);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(customerId)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("userId", user.getId().toString())
                    .putMetadata("plan", plan)
                    .build();

            Session session = Session.create(params);
            log.info("Stripe Checkout Session created for user={} plan={}", userEmail, plan);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Stripe error creating checkout session for user={}: {}", userEmail, e.getMessage());
            throw new BusinessRuleException("Payment processing error: " + e.getUserMessage());
        }
    }

    /**
     * Handles Stripe webhook events to update subscription state.
     * Events processed: checkout.session.completed,
     * customer.subscription.updated, customer.subscription.deleted.
     */
    @Transactional
    public void handleStripeWebhook(String payload, String signature) {
        if (stripeWebhookSecret.isBlank()) {
            log.warn("Stripe webhook received but STRIPE_WEBHOOK_SECRET is not configured — ignoring");
            return;
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature: {}", e.getMessage());
            throw new BusinessRuleException("Invalid webhook signature");
        }

        log.info("Processing Stripe webhook event: type={} id={}", event.getType(), event.getId());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isEmpty()) {
            log.warn("Could not deserialize Stripe event object for event={}", event.getId());
            return;
        }
        StripeObject stripeObject = deserializer.getObject().get();

        switch (event.getType()) {
            case "checkout.session.completed"    -> handleCheckoutCompleted((Session) stripeObject);
            case "customer.subscription.updated" -> handleSubscriptionUpdated((com.stripe.model.Subscription) stripeObject);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted((com.stripe.model.Subscription) stripeObject);
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }
    }

    @Transactional(readOnly = true)
    public Subscription getSubscription(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription for user: " + userEmail));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private String resolvePriceId(String plan) {
        if ("PREMIUM".equalsIgnoreCase(plan) && !premiumPriceId.isBlank()) {
            return premiumPriceId;
        }
        throw new BusinessRuleException("No Stripe price configured for plan: " + plan
                + ". Set STRIPE_PRICE_PREMIUM environment variable.");
    }

    private String resolveOrCreateStripeCustomer(User user) throws StripeException {
        return subscriptionRepository.findByUser(user)
                .map(Subscription::getStripeCustomerId)
                .filter(id -> id != null && !id.isBlank())
                .orElseGet(() -> {
                    try {
                        CustomerCreateParams params = CustomerCreateParams.builder()
                                .setEmail(user.getEmail())
                                .setName(user.getName())
                                .putMetadata("userId", user.getId().toString())
                                .build();
                        Customer customer = Customer.create(params);
                        log.info("Created Stripe customer={} for user={}", customer.getId(), user.getEmail());
                        return customer.getId();
                    } catch (StripeException e) {
                        throw new RuntimeException("Failed to create Stripe customer", e);
                    }
                });
    }

    private void handleCheckoutCompleted(Session session) {
        Map<String, String> metadata = session.getMetadata();
        String userId = metadata.get("userId");
        String plan   = metadata.get("plan");
        if (userId == null || plan == null) {
            log.warn("checkout.session.completed missing metadata userId/plan");
            return;
        }

        userRepository.findById(UUID.fromString(userId)).ifPresent(user -> {
            user.setPlan(User.Plan.valueOf(plan.toUpperCase()));
            userRepository.save(user);

            Subscription sub = subscriptionRepository.findByUser(user)
                    .orElseGet(() -> Subscription.builder().user(user).build());
            sub.setPlan(User.Plan.valueOf(plan.toUpperCase()));
            sub.setStatus(Subscription.Status.ACTIVE);
            sub.setStripeCustomerId(session.getCustomer());
            sub.setStripeSubscriptionId(session.getSubscription());
            subscriptionRepository.save(sub);
            log.info("Subscription activated: user={} plan={}", user.getEmail(), plan);
        });
    }

    private void handleSubscriptionUpdated(com.stripe.model.Subscription stripeSubscription) {
        subscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId()).ifPresent(sub -> {
            sub.setStatus(mapStripeStatus(stripeSubscription.getStatus()));
            if (stripeSubscription.getCurrentPeriodStart() != null) {
                sub.setCurrentPeriodStart(Instant.ofEpochSecond(stripeSubscription.getCurrentPeriodStart()));
            }
            if (stripeSubscription.getCurrentPeriodEnd() != null) {
                sub.setCurrentPeriodEnd(Instant.ofEpochSecond(stripeSubscription.getCurrentPeriodEnd()));
            }
            subscriptionRepository.save(sub);
            log.info("Subscription updated: id={} status={}", stripeSubscription.getId(), stripeSubscription.getStatus());
        });
    }

    private void handleSubscriptionDeleted(com.stripe.model.Subscription stripeSubscription) {
        subscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId()).ifPresent(sub -> {
            sub.setStatus(Subscription.Status.CANCELLED);
            sub.getUser().setPlan(User.Plan.FREE);
            userRepository.save(sub.getUser());
            subscriptionRepository.save(sub);
            log.info("Subscription cancelled: id={} user={}", stripeSubscription.getId(), sub.getUser().getEmail());
        });
    }

    private Subscription.Status mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "active"   -> Subscription.Status.ACTIVE;
            case "past_due" -> Subscription.Status.PAST_DUE;
            case "trialing" -> Subscription.Status.TRIALING;
            default         -> Subscription.Status.CANCELLED;
        };
    }
}
