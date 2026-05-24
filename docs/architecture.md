# Architecture — Backend

## Overview

The Tractor Store backend is a single **Spring Boot 3** application
(`catalog-service`) that exposes a REST/JSON API. It follows a **Modular
Monolith** pattern: each domain module owns its data exclusively and
communicates through in-process domain events, not SQL JOINs.
Schema is managed by **Flyway**; the database is **PostgreSQL 16**.

```
┌────────────────────────────────────────────────────────────┐
│                    catalog-service  (:8080)                │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │  Catalog     │  │  Cart        │  │  Order          │  │
│  │  Controller  │  │  Controller  │  │  Controller     │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬────────┘  │
│         │                 │                    │           │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌────────▼────────┐  │
│  │  Catalog     │  │  Cart        │  │  Order          │  │
│  │  Service     │  │  Service     │  │  Service        │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬────────┘  │
│         │                 │                    │           │
│  ┌──────▼─────────────────▼────────────────────▼────────┐  │
│  │                   PostgreSQL 16                      │  │
│  │  catalog_*   |   cart_*   |   order_*   |   inv_*   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Supporting: ColorDistanceService · EventListeners   │  │
│  │              Flyway migrations                       │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

## Module Data Ownership

Each module owns its tables exclusively. Cross-module SQL JOINs are prohibited.

| Module    | Owned Tables                                                         |
|-----------|----------------------------------------------------------------------|
| Catalog   | `catalog_products`, `catalog_categories`, `catalog_stores`           |
| Inventory | `inventory_stock`                                                    |
| Cart      | `cart_items`                                                         |
| Order     | `order_orders`, `order_items`, `order_outbox_events`                 |

## Layers

| Layer | Package | Responsibility |
|---|---|---|
| Controller | `controller/` | HTTP routing, request/response mapping |
| Service | `service/` | Business logic, orchestration |
| Model | `model/` | Domain records, DTOs, and JPA entities |
| Repository | `repository/` | JPA data access (one per module entity) |
| Event | `event/` | Domain event definitions |
| Listener | `listener/` | Reacts to domain events (AFTER_COMMIT) |
| Config | `config/` | Cross-cutting config (CORS, etc.) |

## Key Design Choices

- **PostgreSQL + Flyway:** Schema is defined in `db/migration/V*.sql` scripts.
  Hibernate uses `ddl-auto=validate`; it never modifies the schema.
- **Module table prefixes:** `catalog_`, `inventory_`, `cart_`, `order_` enforce
  module ownership visually and via ArchUnit (future quality gate).
- **DB-backed Cart:** Cart items are stored in `cart_items` keyed by HTTP
  session ID. Items persist across restarts and are cleared after order placement.
- **Outbox Pattern:** `order_outbox_events` is written in the same transaction as
  `order_orders`, guaranteeing consistency without distributed locks.
- **REQUIRES_NEW listeners:** `CartEventListener` and `InventoryEventListener`
  run in a new transaction after order commit to avoid sharing the order TX.
- **Native types:** `UUID` for IDs, `NUMERIC(12,2)` for money,
  `TIMESTAMPTZ` for all timestamps, `JSONB` for outbox payloads.
- **Color-based recommendations:** `ColorDistanceService` computes Euclidean RGB
  distance between product colors to return the 6 most visually similar products.
- **CORS:** `WebConfig` enables cross-origin requests from the three MFE dev servers.
- **HikariCP:** Configured with `maximum-pool-size=10`, `minimum-idle=2`.

## Related ADRs

- [ADR-0001 — Single Spring Boot service](adr/0001-single-spring-boot-service.md)
- [ADR-0003 — PostgreSQL with Flyway](adr/0003-postgresql-migration.md)

