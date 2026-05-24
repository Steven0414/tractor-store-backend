# Catalog Service — Internal Design

## Overview

`catalog-service` is the catalog microservice of the Tractor Store. It is a
**Spring Boot 3 / Java 17** application on **port 8080** that exposes product
catalog, categories, store locations, and color-based recommendations. It is
also the **schema owner**: it runs Flyway migrations and creates all database
tables used by every service.

It is consumed by `mfe-explore` and `mfe-decide` microfrontends.

## Domain Model

```
Product   — catalog item (id, sku, name, price, imageUrl, category, color, motor)
Category  — product grouping (id, name, filter, imageUrl, description)
Store     — physical store location (id, name, address, city, phone, email, lat, lon, openingHours)
HomeData  — aggregated home page payload (bannerTitle, bannerSubtitle, featuredCategories, featuredProducts)
```

## Services

### `CatalogService`
- Reads products, categories, stores from the database via JPA repositories.
- `getHome()` — returns banner + featured categories + first 4 products.
- `getProductsByFilter(filter)` — returns all products for a category; `"all"` returns everything.
- `getProductBySku(sku)` — returns a single product; throws `NoSuchElementException` if not found.
- `getStores()` — returns all physical store locations.
- `getRecommendations(skusCsv)` — delegates color-similarity ranking to `ColorDistanceService`.

### `ColorDistanceService`
- Parses hex color strings (e.g. `#CC0000`).
- Computes Euclidean distance in RGB space.
- Used by `getRecommendations()` to return up to 6 most visually similar products.

## Schema Ownership

`catalog-service` runs the **Flyway migrations** that create tables for ALL modules:

```
db/migration/
├── V1__initial_schema.sql      ← All tables (catalog_*, inventory_*, cart_*, order_*)
├── V2__seed_catalog_data.sql   ← 20 products, 2 categories, 5 stores
└── V3__seed_inventory_stock.sql ← Initial inventory (10 units/SKU)
```

All other services set `spring.flyway.enabled=false` and `spring.jpa.hibernate.ddl-auto=validate`.

## Configuration

| Property | Value | Purpose |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.flyway.enabled` | `true` | Runs migrations on startup |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Hibernate validates (never modifies) schema |

## CORS

`WebConfig` allows `http://localhost:*` origins for `/api/**`. Restrict to production domains before deploying.
