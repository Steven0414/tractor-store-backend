# Architecture — Backend

## Overview

The Tractor Store backend has been migrated from a **Modular Monolith**
(`catalog-service`) to **5 independent Spring Boot 3 microservices**. Each
service owns its REST API, runs on a dedicated port, and deploys as its own
Docker container. All services share a single **PostgreSQL 16** database
(`tractordb`). Cross-module SQL JOINs are still prohibited — table-prefix
conventions enforce module ownership at the naming level. Schema creation and
all Flyway migrations are managed exclusively by `catalog-service`; the other
four services set `spring.flyway.enabled=false` and `ddl-auto=validate`.

## Service Map

```
  Frontend (MFEs)
       │
       ▼
┌─────────────────┐   ┌──────────────────┐   ┌─────────────────┐
│  catalog-service │   │ inventory-service │   │   cart-service  │
│     (:8080)      │   │     (:8081)       │   │    (:8082)      │
│                  │   │                  │   │                 │
│  GET /catalog/*  │   │ GET /inventory/  │   │ GET/POST/DELETE │
│                  │   │     /{sku}       │   │   /cart/*       │
└─────────────────┘   └──────────────────┘   └─────────────────┘
         │                     ▲                      ▲
         │                     │  POST /internal/     │
         │              ┌──────┴──────────────────────┴──────┐
         │              │         order-service (:8083)       │
         │              │  POST /api/orders                   │
         │              │  GET  /api/orders/{id}              │
         │              └──────────────────┬─────────────────┘
         │                                 │  POST /internal/
         │                    ┌────────────▼───────────┐
         │                    │  notifications-service  │
         │                    │       (:8084)           │
         │                    │  (email simulation)     │
         │                    └────────────────────────┘
         │
         ▼
    PostgreSQL 16 (tractordb)
    catalog_* | inventory_* | cart_* | order_*
```

## Module Data Ownership

Each service owns its tables exclusively. Cross-module SQL JOINs are prohibited.

| Module        | Owned Tables                                                         |
|---------------|----------------------------------------------------------------------|
| Catalog       | `catalog_products`, `catalog_categories`, `catalog_stores`           |
| Inventory     | `inventory_stock`                                                    |
| Cart          | `cart_items`                                                         |
| Order         | `order_orders`, `order_items`, `order_outbox_events`                 |

## Event Flow

Order placement uses **HTTP-based choreography**. After a successful order
transaction commits, `order-service` publishes an `OrderPlaced` event by
calling `POST /internal/events/order-placed` on each registered subscriber.

The listener is annotated with `@TransactionalEventListener(AFTER_COMMIT)`,
guaranteeing that the HTTP fanout only fires once the DB transaction is durable.

Subscribers are configured via the `order.events.order-placed.subscribers`
property — adding a new subscriber requires **zero code changes**.

| Subscriber              | Reaction                                        |
|-------------------------|-------------------------------------------------|
| `inventory-service`     | Deducts stock for each ordered SKU              |
| `cart-service`          | Clears the session cart for the placing user    |
| `notifications-service` | Logs a simulated confirmation email (no DB)     |

## Key Design Choices

- **PostgreSQL + Flyway:** Schema is defined in `catalog-service`'s
  `db/migration/V*.sql` scripts. All services use `ddl-auto=validate`; only
  `catalog-service` sets `spring.flyway.enabled=true`.
- **Schema ownership:** `catalog-service` is the sole schema owner. Other
  services connect to the same `tractordb` database but never modify the schema.
- **Module table prefixes:** `catalog_`, `inventory_`, `cart_`, `order_` enforce
  module ownership visually and via ArchUnit (future quality gate).
- **DB-backed Cart:** Cart items are stored in `cart_items` keyed by HTTP
  session ID. Items persist across restarts and are cleared after order placement.
- **Outbox Pattern:** `order_outbox_events` is written in the same transaction as
  `order_orders`, guaranteeing consistency without distributed locks.
- **Config-driven event subscribers:** The list of services notified on
  `OrderPlaced` lives in application properties. No code change is needed to
  add or remove a subscriber — open/closed extension by configuration.
- **Native types:** `UUID` for IDs, `NUMERIC(12,2)` for money,
  `TIMESTAMPTZ` for all timestamps, `JSONB` for outbox payloads.
- **Color-based recommendations:** `ColorDistanceService` (in `catalog-service`)
  computes Euclidean RGB distance between product colors to return the 6 most
  visually similar products.
- **CORS:** Each service's `WebConfig` enables cross-origin requests from the
  three MFE dev servers.
- **HikariCP:** Configured with `maximum-pool-size=10`, `minimum-idle=2`.

## Related ADRs

- [ADR-0001 — Single Spring Boot service](adr/0001-single-spring-boot-service.md) *(Superseded)*
- [ADR-0002 — PostgreSQL with Flyway](adr/0002-postgresql-migration.md)
- [ADR-0003 — Decomposition into Microservices](adr/0003-microservices-decomposition.md)

