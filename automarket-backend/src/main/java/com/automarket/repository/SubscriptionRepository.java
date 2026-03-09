package com.automarket.repository;

import com.automarket.entity.Subscription;
import com.automarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUser(User user);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
