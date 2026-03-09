-- ═══════════════════════════════════════════════════════════════════
-- V3: Listings and listing images
-- Key improvements over legacy:
--   - All numeric fields (price, km, year, kW) are proper types, not strings
--   - Slugs for SEO-friendly URLs
--   - JSONB attributes for AI/ML extensibility
--   - Soft delete via deleted_at
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE listings (
    -- Identity
    id                   UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    title                VARCHAR(100)   NOT NULL,
    slug                 VARCHAR(150)   NOT NULL UNIQUE,
    description          TEXT           NOT NULL,

    -- Pricing
    price                NUMERIC(12, 2) NOT NULL CHECK (price >= 0),

    -- Condition
    condition_type_id    UUID           REFERENCES condition_types(id) ON DELETE SET NULL,

    -- Moderation
    approved             BOOLEAN        NOT NULL DEFAULT false,

    -- Featured / monetization
    featured             BOOLEAN        NOT NULL DEFAULT false,
    featured_until       TIMESTAMPTZ,

    -- Seller
    seller_id            UUID           NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- ── Embedded car details ─────────────────────────────────────
    car_brand_id         UUID           REFERENCES car_brands(id) ON DELETE SET NULL,
    car_model            VARCHAR(100)   NOT NULL,
    registration_year    SMALLINT       NOT NULL CHECK (registration_year BETWEEN 1886 AND 2100),
    kilometers           INTEGER        NOT NULL CHECK (kilometers >= 0),
    fuel_type_id         UUID           REFERENCES fuel_types(id) ON DELETE SET NULL,
    body_type_id         UUID           REFERENCES body_types(id) ON DELETE SET NULL,
    transmission_type_id UUID           REFERENCES transmission_types(id) ON DELETE SET NULL,
    num_doors            SMALLINT       CHECK (num_doors BETWEEN 1 AND 10),
    num_seats            SMALLINT       CHECK (num_seats BETWEEN 1 AND 20),
    kilowatts            SMALLINT       CHECK (kilowatts > 0),

    -- AI / extensibility
    attributes           JSONB          NOT NULL DEFAULT '{}',

    -- Audit
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    deleted_at           TIMESTAMPTZ
);

-- Search and filter indexes
CREATE INDEX idx_listings_seller        ON listings(seller_id);
CREATE INDEX idx_listings_approved      ON listings(approved) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_featured      ON listings(featured, featured_until) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_price         ON listings(price) WHERE deleted_at IS NULL AND approved = true;
CREATE INDEX idx_listings_year          ON listings(registration_year) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_brand         ON listings(car_brand_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_city          ON listings(seller_id) WHERE deleted_at IS NULL; -- via seller join
CREATE INDEX idx_listings_deleted       ON listings(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_slug          ON listings(slug);
CREATE INDEX idx_listings_attributes    ON listings USING GIN (attributes);

CREATE TABLE listing_images (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id    UUID         NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    storage_key   VARCHAR(512) NOT NULL,
    url           VARCHAR(1024) NOT NULL,
    display_order SMALLINT     NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_listing_images_listing ON listing_images(listing_id, display_order);
