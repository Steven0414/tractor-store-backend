# Database Documentation

## Overview

| Property | Value |
|---|---|
| Engine | PostgreSQL |
| Database name | `tractordb` |
| Default host | `localhost:5432` |
| Migration tool | [Flyway](https://flywaydb.org/) |
| ORM | Hibernate / JPA (schema read-only — `ddl-auto=validate`) |

Schema changes are managed exclusively through Flyway migrations located at:

```
catalog-service/src/main/resources/db/migration/
├── V1__initial_schema.sql    # Table and index definitions
├── V2__seed_catalog_data.sql # Products, categories, and stores
└── V3__seed_inventory_stock.sql # Initial stock for all SKUs
```

---

## Architecture

The database follows a **Modular Monolith** pattern: each business module owns its tables exclusively and communicates only through domain events. Tables are prefixed by module to make boundaries explicit.

```
tractordb
├── catalog_*      — Catalog module
├── inventory_*    — Inventory module
├── cart_*         — Cart module
└── order_*        — Order module
```

---

## Tables

### Module: catalog

#### `catalog_products`

Stores the product catalogue (tractors).

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `sku` | `VARCHAR(20)` | NOT NULL, UNIQUE | Product stock-keeping unit code |
| `name` | `VARCHAR(255)` | NOT NULL | Display name |
| `price` | `NUMERIC(12,2)` | NOT NULL | Unit price in COP |
| `image_url` | `TEXT` | — | URL of the product image |
| `category` | `VARCHAR(50)` | NOT NULL | Category filter slug (e.g. `classic`, `autonomous`) |
| `color` | `VARCHAR(7)` | — | Hex color code (e.g. `#CC0000`) |
| `motor` | `VARCHAR(100)` | — | Engine description (e.g. `Diesel 75HP`) |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_catalog_products_sku` | `sku` | Unique lookup by SKU |
| `idx_catalog_products_category` | `category` | Filter by category |

**Seed data** — 20 products across two categories:

| SKU range | Category | Motor type |
|---|---|---|
| `TRK-001` – `TRK-010` | `classic` | Diesel / Gasolina |
| `AUT-001` – `AUT-010` | `autonomous` | Eléctrico |

---

#### `catalog_categories`

Stores browsable product categories.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `name` | `VARCHAR(255)` | NOT NULL | Display name |
| `filter` | `VARCHAR(50)` | NOT NULL, UNIQUE | URL-safe slug used to filter products |
| `image_url` | `TEXT` | — | Category banner image URL |
| `description` | `TEXT` | — | Short marketing description |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_catalog_categories_filter` | `filter` | Lookup category by slug |

**Seed data**

| filter | name |
|---|---|
| `classic` | Tractores Clásicos |
| `autonomous` | Tractores Autónomos |

---

#### `catalog_stores`

Stores physical retail locations.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `name` | `VARCHAR(255)` | NOT NULL | Store display name |
| `address` | `VARCHAR(255)` | — | Street address |
| `city` | `VARCHAR(100)` | NOT NULL | City |
| `phone` | `VARCHAR(30)` | — | Contact phone number |
| `email` | `VARCHAR(255)` | — | Contact email |
| `latitude` | `DOUBLE PRECISION` | NOT NULL, DEFAULT `0` | GPS latitude |
| `longitude` | `DOUBLE PRECISION` | NOT NULL, DEFAULT `0` | GPS longitude |
| `opening_hours` | `VARCHAR(100)` | — | Human-readable schedule (e.g. `Lun-Sáb 8:00-18:00`) |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_catalog_stores_city` | `city` | Filter stores by city |

**Seed data** — 5 stores: Bogotá, Medellín, Cali, Barranquilla, Cartagena.

---

### Module: inventory

#### `inventory_stock`

Tracks available stock per SKU.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `sku` | `VARCHAR(20)` | NOT NULL, UNIQUE | Product SKU (mirrors `catalog_products.sku`) |
| `quantity` | `INTEGER` | NOT NULL, DEFAULT `0`, CHECK `>= 0` | Units available |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Timestamp of last stock update |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_inventory_stock_sku` | `sku` | Unique; primary lookup path |

**Seed data** — All 20 SKUs start with `quantity = 10`.

> **Note:** `inventory_stock` does not declare a foreign key to `catalog_products` by design. Module isolation is enforced at the application layer; the `sku` field acts as a logical reference.

---

### Module: cart

#### `cart_items`

Persists shopping cart contents, keyed by browser session.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `session_id` | `VARCHAR(128)` | NOT NULL | Browser/client session identifier |
| `sku` | `VARCHAR(20)` | NOT NULL | Product SKU |
| `name` | `VARCHAR(255)` | NOT NULL | Product name snapshot at time of adding |
| `quantity` | `INTEGER` | NOT NULL, CHECK `> 0` | Quantity added |
| `price` | `NUMERIC(12,2)` | NOT NULL | Unit price snapshot at time of adding |
| `added_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Timestamp when the item was added |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_cart_items_session_id` | `session_id` | Retrieve all items for a given session |

---

### Module: order

#### `order_orders`

Master record for placed orders.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK | Application-generated order ID |
| `first_name` | `VARCHAR(100)` | NOT NULL | Customer first name |
| `last_name` | `VARCHAR(100)` | NOT NULL | Customer last name |
| `email` | `VARCHAR(255)` | NOT NULL | Customer email |
| `phone` | `VARCHAR(30)` | NOT NULL | Customer phone number |
| `address` | `VARCHAR(255)` | NOT NULL | Shipping address |
| `city` | `VARCHAR(100)` | NOT NULL | Shipping city |
| `postal_code` | `VARCHAR(20)` | NOT NULL | Shipping postal code |
| `payment_method` | `VARCHAR(50)` | NOT NULL | Payment method (e.g. `CREDIT_CARD`) |
| `total` | `NUMERIC(12,2)` | NOT NULL | Order grand total in COP |
| `status` | `VARCHAR(30)` | NOT NULL, DEFAULT `'CONFIRMED'` | Order lifecycle status |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Order creation timestamp |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_order_orders_created_at` | `created_at` | Chronological order listings |

---

#### `order_items`

Line items belonging to an order.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `order_id` | `UUID` | NOT NULL, FK → `order_orders(id)` | Parent order reference |
| `sku` | `VARCHAR(20)` | NOT NULL | Product SKU snapshot |
| `name` | `VARCHAR(255)` | NOT NULL | Product name snapshot |
| `quantity` | `INTEGER` | NOT NULL, CHECK `> 0` | Units ordered |
| `price` | `NUMERIC(12,2)` | NOT NULL | Unit price at time of order |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_order_items_order_id` | `order_id` | Retrieve all items for an order |

---

#### `order_outbox_events`

Implements the [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html) to reliably publish domain events.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `UUID` | PK, `DEFAULT gen_random_uuid()` | Surrogate primary key |
| `aggregate_type` | `VARCHAR(50)` | NOT NULL | Domain aggregate type (e.g. `Order`) |
| `aggregate_id` | `VARCHAR(255)` | NOT NULL | ID of the aggregate that produced the event |
| `event_type` | `VARCHAR(50)` | NOT NULL | Event name (e.g. `ORDER_PLACED`) |
| `payload` | `JSONB` | NOT NULL | Full event payload in JSON |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Event creation timestamp |
| `processed` | `BOOLEAN` | NOT NULL, DEFAULT `false` | Whether the event has been dispatched |

**Indexes**

| Index | Columns | Notes |
|---|---|---|
| `idx_outbox_processed` | `processed` | Efficiently poll for unprocessed events |
| `idx_outbox_aggregate_id` | `aggregate_id` | Look up events by aggregate |

---

## Entity Relationship Diagram

```
catalog_products ──(sku)──► inventory_stock
catalog_products ──(sku)──► cart_items        (logical, no FK)
catalog_products ──(sku)──► order_items       (logical, no FK)
catalog_categories ─(filter)─► catalog_products.category (logical, no FK)

order_orders ──(id)──► order_items            (FK enforced)
order_orders ──(id)──► order_outbox_events.aggregate_id (logical)
```

> Cross-module references (e.g., `inventory_stock.sku` → `catalog_products.sku`) are intentionally kept as **logical references only** (no database-level FK). This preserves module autonomy in the modular monolith design.

---

## Connection Configuration

Connection parameters are injected via environment variables or `application.properties`:

| Property | Environment Variable | Default |
|---|---|---|
| JDBC URL | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tractordb` |
| Username | `SPRING_DATASOURCE_USERNAME` | `tractoruser` |
| Password | `SPRING_DATASOURCE_PASSWORD` | `tractorpass` |

**HikariCP pool settings:**

| Setting | Value |
|---|---|
| `maximum-pool-size` | 10 |
| `minimum-idle` | 2 |
| `idle-timeout` | 30 000 ms |
| `connection-timeout` | 20 000 ms |
| `max-lifetime` | 1 800 000 ms (30 min) |

---

## Migration History

| Version | File | Description |
|---|---|---|
| V1 | `V1__initial_schema.sql` | All table and index definitions |
| V2 | `V2__seed_catalog_data.sql` | 20 products, 2 categories, 5 stores |
| V3 | `V3__seed_inventory_stock.sql` | Initial stock (10 units per SKU) |
