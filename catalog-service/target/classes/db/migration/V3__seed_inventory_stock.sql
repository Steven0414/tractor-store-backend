-- ============================================================
-- V3 — Seed initial inventory stock
-- Owned by the inventory module (inventory_stock table).
-- Each SKU starts with 10 units in stock.
-- ============================================================

INSERT INTO inventory_stock (sku, quantity, updated_at) VALUES
    ('TRK-001', 10, now()),
    ('TRK-002', 10, now()),
    ('TRK-003', 10, now()),
    ('TRK-004', 10, now()),
    ('TRK-005', 10, now()),
    ('TRK-006', 10, now()),
    ('TRK-007', 10, now()),
    ('TRK-008', 10, now()),
    ('TRK-009', 10, now()),
    ('TRK-010', 10, now()),
    ('AUT-001', 10, now()),
    ('AUT-002', 10, now()),
    ('AUT-003', 10, now()),
    ('AUT-004', 10, now()),
    ('AUT-005', 10, now()),
    ('AUT-006', 10, now()),
    ('AUT-007', 10, now()),
    ('AUT-008', 10, now()),
    ('AUT-009', 10, now()),
    ('AUT-010', 10, now());
