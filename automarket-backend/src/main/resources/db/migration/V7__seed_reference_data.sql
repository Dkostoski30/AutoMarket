-- ═══════════════════════════════════════════════════════════════════
-- V7: Seed reference data (lookup tables)
-- ═══════════════════════════════════════════════════════════════════

-- Cities (Macedonian cities as per original project)
INSERT INTO cities (name) VALUES
    ('Skopje'), ('Bitola'), ('Kumanovo'), ('Prilep'), ('Tetovo'),
    ('Veles'), ('Štip'), ('Ohrid'), ('Gostivar'), ('Strumica'),
    ('Kavadarci'), ('Kočani'), ('Kičevo'), ('Struga'), ('Radoviš'),
    ('Gevgelija'), ('Debar'), ('Kriva Palanka'), ('Negotino'), ('Vinica');

-- Car Brands
INSERT INTO car_brands (name) VALUES
    ('Audi'), ('BMW'), ('Mercedes-Benz'), ('Volkswagen'), ('Toyota'),
    ('Honda'), ('Ford'), ('Opel'), ('Peugeot'), ('Renault'),
    ('Skoda'), ('Seat'), ('Fiat'), ('Hyundai'), ('Kia'),
    ('Nissan'), ('Mazda'), ('Volvo'), ('Porsche'), ('Land Rover'),
    ('Jeep'), ('Mitsubishi'), ('Suzuki'), ('Subaru'), ('Lexus'),
    ('Alfa Romeo'), ('Citroën'), ('Dacia'), ('Mini'), ('Tesla');

-- Fuel Types
INSERT INTO fuel_types (name) VALUES
    ('Petrol'), ('Diesel'), ('Electric'), ('Hybrid'), ('Plug-in Hybrid'),
    ('LPG'), ('CNG'), ('Hydrogen');

-- Body Types
INSERT INTO body_types (name) VALUES
    ('Sedan'), ('Hatchback'), ('SUV'), ('Coupe'), ('Estate / Wagon'),
    ('Convertible'), ('Van'), ('Pickup'), ('Minivan'), ('Roadster');

-- Condition Types
INSERT INTO condition_types (name) VALUES
    ('New'), ('Used'), ('Certified Pre-Owned'), ('Salvage');

-- Transmission Types
INSERT INTO transmission_types (name) VALUES
    ('Manual'), ('Automatic'), ('Semi-Automatic'), ('CVT'), ('Dual-Clutch (DCT)');
