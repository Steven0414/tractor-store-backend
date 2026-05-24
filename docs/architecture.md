# Architecture — Backend

## Overview

The Tractor Store backend runs as a **single Spring Boot modular monolith**
(`tractor-store`) with DDD module boundaries and Clean Architecture layers per
module. The runtime is one deployable unit on port `8080`, with PostgreSQL 16
(`tractordb`) as the persistence layer.

## Module Map

```
  Frontend (MFEs)
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│                  tractor-store (:8080)                     │
│                                                             │
│  catalog      inventory      cart      order   notifications│
│    │              │            │         │           │      │
│    └──────────────┴────────────┴─────────┴───────────┘      │
│                  In-process domain events                   │
└─────────────────────────────────────────────────────────────┘
           │
           ▼
      PostgreSQL 16 (tractordb)
      catalog_* | inventory_* | cart_* | order_*
```

## Module Data Ownership

Each module owns its tables exclusively. Cross-module SQL JOINs are prohibited.

| Module        | Owned Tables                                                         |
|---------------|----------------------------------------------------------------------|
| Catalog       | `catalog_products`, `catalog_categories`, `catalog_stores`           |
| Inventory     | `inventory_stock`                                                    |
| Cart          | `cart_items`                                                         |
| Order         | `order_orders`, `order_items`, `order_outbox_events`                 |

## Event Flow

Order placement uses **in-process event choreography**. After order persistence,
the `order` module emits an internal event and publishes `OrderPlaced` only on
`AFTER_COMMIT`, ensuring durable state before side effects.

`cart`, `inventory`, and `notifications` subscribe to `OrderPlaced` and react
without coupling to `order` internals.

| Subscriber              | Reaction                                        |
|-------------------------|-------------------------------------------------|
| `inventory`             | Deducts stock for each ordered SKU              |
| `cart`                  | Clears the session cart for the placing user    |
| `notifications`         | Logs a simulated confirmation email (no DB)     |

## Key Design Choices

- **PostgreSQL + Flyway:** Schema is managed by `tractor-store` under
  `src/main/resources/db/migration/V*.sql`.
- **Schema ownership:** the modular monolith owns the full schema in one place,
  preserving module boundaries by table prefix and package boundaries.
- **Module table prefixes:** `catalog_`, `inventory_`, `cart_`, `order_` enforce
  module ownership visually and via ArchUnit (future quality gate).
- **DB-backed Cart:** Cart items are stored under `cart_*` tables keyed by
  HTTP session ID and are cleared after order placement.
- **Outbox Pattern:** `order_outbox_events` is written in the same transaction as
  `order_orders`, guaranteeing consistency without distributed locks.
- **Transactional events:** `@TransactionalEventListener(AFTER_COMMIT)` ensures
  downstream reactions only after durable commit.
- **Quality gates:** Spring Modulith `ApplicationModules.verify()` and ArchUnit
  prevent accidental cross-module leakage.

## Related ADRs

- [ADR-0001 — Single Spring Boot service](adr/0001-single-spring-boot-service.md) *(Superseded)*
- [ADR-0002 — PostgreSQL with Flyway](adr/0002-postgresql-migration.md)
- [ADR-0003 — Decomposition into Microservices](adr/0003-microservices-decomposition.md) *(Superseded)*
- [ADR-0004 — Recomposition to Modular Monolith](adr/0004-modular-monolith-recomposition.md)

