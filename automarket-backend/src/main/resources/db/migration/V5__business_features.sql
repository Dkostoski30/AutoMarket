-- ═══════════════════════════════════════════════════════════════════
-- V5: Business features — subscriptions, favorites, inquiries, analytics
-- ═══════════════════════════════════════════════════════════════════

-- Subscriptions (monetization)
CREATE TABLE subscriptions (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID        NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    plan                    VARCHAR(20) NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    stripe_subscription_id  VARCHAR(255),
    stripe_customer_id      VARCHAR(255),
    current_period_start    TIMESTAMPTZ,
    current_period_end      TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255)
);

CREATE INDEX idx_subscriptions_user       ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_stripe     ON subscriptions(stripe_subscription_id);

-- Favorites
CREATE TABLE favorites (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id UUID        NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, listing_id)
);

CREATE INDEX idx_favorites_user    ON favorites(user_id, created_at DESC);
CREATE INDEX idx_favorites_listing ON favorites(listing_id);

-- Inquiries
CREATE TABLE inquiries (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id       UUID        NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    sender_id        UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message          TEXT        NOT NULL,
    read_by_seller   BOOLEAN     NOT NULL DEFAULT false,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inquiries_listing ON inquiries(listing_id);
CREATE INDEX idx_inquiries_sender  ON inquiries(sender_id);

-- Listing analytics (daily rollup)
CREATE TABLE listing_analytics (
    id              UUID     PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id      UUID     NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    date            DATE     NOT NULL,
    view_count      INTEGER  NOT NULL DEFAULT 0,
    inquiry_count   INTEGER  NOT NULL DEFAULT 0,
    favorite_count  INTEGER  NOT NULL DEFAULT 0,
    UNIQUE (listing_id, date)
);

CREATE INDEX idx_analytics_listing ON listing_analytics(listing_id, date DESC);
