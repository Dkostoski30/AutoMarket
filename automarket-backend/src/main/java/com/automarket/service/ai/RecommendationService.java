package com.automarket.service.ai;

import java.util.List;
import java.util.UUID;

/**
 * AI recommendation service interface.
 * Default implementation ({@link NoOpRecommendationService}) returns empty list.
 * Plug in a real ML model (e.g., collaborative filtering, embeddings) without changing any controller.
 * Enable via {@code automarket.features.ai-recommendations=true}.
 */
public interface RecommendationService {
    List<UUID> getRecommendations(UUID listingId, UUID userId, int limit);
}
