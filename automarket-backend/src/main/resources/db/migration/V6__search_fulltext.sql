-- ═══════════════════════════════════════════════════════════════════
-- V6: Full-text search vector on listings
-- ═══════════════════════════════════════════════════════════════════

ALTER TABLE listings ADD COLUMN search_vector TSVECTOR;

-- Populate initial values
UPDATE listings
SET search_vector = to_tsvector('english',
    coalesce(title, '') || ' ' ||
    coalesce(description, '') || ' ' ||
    coalesce(car_model, '')
);

-- GIN index for fast full-text search
CREATE INDEX idx_listings_search ON listings USING GIN (search_vector);

-- Trigger to keep search_vector up to date on insert/update
CREATE OR REPLACE FUNCTION listings_search_vector_update() RETURNS trigger AS $$
BEGIN
    NEW.search_vector := to_tsvector('english',
        coalesce(NEW.title, '') || ' ' ||
        coalesce(NEW.description, '') || ' ' ||
        coalesce(NEW.car_model, '')
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER listings_search_vector_trigger
BEFORE INSERT OR UPDATE OF title, description, car_model
ON listings
FOR EACH ROW EXECUTE FUNCTION listings_search_vector_update();
