package com.automarket.service.ai;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/**
 * No-op recommendation service — active until a real AI model is integrated.
 * Returns empty list; Angular components gracefully hide the recommendations section.
 */
@Service
public class NoOpRecommendationService implements RecommendationService {
    @Override
    public List<UUID> getRecommendations(UUID listingId, UUID userId, int limit) {
        return List.of();
    }
}
