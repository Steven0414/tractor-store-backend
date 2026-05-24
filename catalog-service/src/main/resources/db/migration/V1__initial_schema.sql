-- ============================================================
-- V1 — Initial schema
-- Modular Monolith: each module owns its tables exclusively.
-- Prefixes: catalog_, inventory_, cart_, order_
-- ============================================================

-- ──────────────────────────────
-- MODULE: catalog
-- ──────────────────────────────

CREATE TABLE catalog_products (
    id         UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    sku        VARCHAR(20)  NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    price      NUMERIC(12,2) NOT NULL,
    image_url  TEXT,
    category   VARCHAR(50)  NOT NULL,
    color      VARCHAR(7),
    motor      VARCHAR(100)
);

CREATE INDEX idx_catalog_products_sku      ON catalog_products (sku);
CREATE INDEX idx_catalog_products_category ON catalog_products (category);

CREATE TABLE catalog_categories (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    filter      VARCHAR(50)  NOT NULL UNIQUE,
    image_url   TEXT,
    description TEXT
);

CREATE INDEX idx_catalog_categories_filter ON catalog_categories (filter);

CREATE TABLE catalog_stores (
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    address       VARCHAR(255),
    city          VARCHAR(100) NOT NULL,
    phone         VARCHAR(30),
    email         VARCHAR(255),
    latitude      DOUBLE PRECISION NOT NULL DEFAULT 0,
    longitude     DOUBLE PRECISION NOT NULL DEFAULT 0,
    opening_hours VARCHAR(100)
);

CREATE INDEX idx_catalog_stores_city ON catalog_stores (city);

-- ──────────────────────────────
-- MODULE: inventory
-- ──────────────────────────────

CREATE TABLE inventory_stock (
    id         UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    sku        VARCHAR(20) NOT NULL UNIQUE,
    quantity   INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_inventory_stock_sku ON inventory_stock (sku);

-- ──────────────────────────────
-- MODULE: cart
-- ──────────────────────────────

CREATE TABLE cart_items (
    id         UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    session_id VARCHAR(128)  NOT NULL,
    sku        VARCHAR(20)   NOT NULL,
    name       VARCHAR(255)  NOT NULL,
    quantity   INTEGER       NOT NULL CHECK (quantity > 0),
    price      NUMERIC(12,2) NOT NULL,
    added_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_cart_items_session_id ON cart_items (session_id);

-- ──────────────────────────────
-- MODULE: order
-- ──────────────────────────────

CREATE TABLE order_orders (
    id             UUID          NOT NULL PRIMARY KEY,
    first_name     VARCHAR(100)  NOT NULL,
    last_name      VARCHAR(100)  NOT NULL,
    email          VARCHAR(255)  NOT NULL,
    phone          VARCHAR(30)   NOT NULL,
    address        VARCHAR(255)  NOT NULL,
    city           VARCHAR(100)  NOT NULL,
    postal_code    VARCHAR(20)   NOT NULL,
    payment_method VARCHAR(50)   NOT NULL,
    total          NUMERIC(12,2) NOT NULL,
    status         VARCHAR(30)   NOT NULL DEFAULT 'CONFIRMED',
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_orders_created_at ON order_orders (created_at);

CREATE TABLE order_items (
    id       UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID          NOT NULL REFERENCES order_orders(id),
    sku      VARCHAR(20)   NOT NULL,
    name     VARCHAR(255)  NOT NULL,
    quantity INTEGER       NOT NULL CHECK (quantity > 0),
    price    NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

CREATE TABLE order_outbox_events (
    id             UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    event_type     VARCHAR(50) NOT NULL,
    payload        JSONB       NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed      BOOLEAN     NOT NULL DEFAULT false
);

CREATE INDEX idx_outbox_processed    ON order_outbox_events (processed);
CREATE INDEX idx_outbox_aggregate_id ON order_outbox_events (aggregate_id);
