-- ═══════════════════════════════════════════════════════════════════
-- V4: Blog posts
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE blogs (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    image_url  VARCHAR(1024),
    author_id  UUID         REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255)
);

CREATE INDEX idx_blogs_created_at ON blogs(created_at DESC);
