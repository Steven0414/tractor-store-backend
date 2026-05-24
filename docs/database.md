# Database Documentation

## Overview

| Property | Value |
|---|---|
| Engine | PostgreSQL 16 |
| Database name | `tractordb` |
| Migration tool | Flyway |
| Runtime | `tractor-store` modular monolith |

Flyway migrations live in:

```text
tractor-store/src/main/resources/db/migration/
  V1__init_modular_monolith.sql
```

## Table ownership by module

| Module | Tables |
|---|---|
| Catalog | `catalog_products`, `catalog_stores` |
| Inventory | `inventory_items` |
| Cart | `cart_carts`, `cart_items` |
| Order | `order_orders`, `order_order_lines`, `order_outbox_events` |

Cross-module SQL joins are prohibited by architecture convention.

## Core tables

### `catalog_products`
- `id` bigserial PK
- `sku` varchar(100) unique
- `name` varchar(255)
- `category` varchar(120)
- `price` numeric(12,2)

### `catalog_stores`
- `id` bigserial PK
- `code` varchar(50) unique
- `name` varchar(255)
- `city` varchar(120)

### `inventory_items`
- `id` bigserial PK
- `sku` varchar(100) unique
- `available` int

### `cart_carts`
- `id` bigserial PK
- `session_id` varchar(100) unique

### `cart_items`
- `id` bigserial PK
- `cart_id` bigint FK -> `cart_carts(id)`
- `sku` varchar(100)
- `quantity` int
- `unit_price` numeric(12,2)

### `order_orders`
- `id` bigserial PK
- `session_id` varchar(100)
- `pickup_store_code` varchar(50)
- `buyer_name` varchar(255)
- `buyer_email` varchar(255)
- `status` varchar(30)
- `created_at` timestamptz

### `order_order_lines`
- `id` bigserial PK
- `order_id` bigint FK -> `order_orders(id)`
- `sku` varchar(100)
- `quantity` int

### `order_outbox_events`
- `id` bigserial PK
- `aggregate_type` varchar(50)
- `aggregate_id` varchar(80)
- `event_type` varchar(100)
- `payload` text
- `status` varchar(30)
- `created_at` timestamptz

## Seed data

`V1__init_modular_monolith.sql` also inserts starter records:
- 3 products
- 2 stores
- 3 inventory entries

## Runtime configuration

Datasource properties in `tractor-store/src/main/resources/application.properties`:

- `spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/tractordb}`
- `spring.datasource.username=${DB_USER:postgres}`
- `spring.datasource.password=${DB_PASSWORD:postgres}`
- `spring.jpa.hibernate.ddl-auto=validate`
