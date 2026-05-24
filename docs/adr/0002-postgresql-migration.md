# ADR-0002 — PostgreSQL with Flyway (Modular Monolith Data Architecture)

## Status

Accepted

## Context

ADR-0002 established H2 in-memory as a pragmatic choice for early development.
As the project matures toward a production-grade Modular Monolith, H2 presents
critical limitations:

- Data is lost on restart; cart sessions and orders cannot survive deployment.
- H2 SQL dialect diverges from PostgreSQL, masking real production issues.
- Native PostgreSQL types (JSONB, TIMESTAMPTZ, NUMERIC) are required for
  correctness and performance at scale.
- Schema evolution needs a controlled migration tool (Flyway) rather than
  `ddl-auto=create-drop`.

## Decision

Migrate the backend to **PostgreSQL 16** managed by **Flyway**, following the
Modular Monolith data architecture principles:

### 1. Module Ownership and Table Prefixes

Each logical module owns its tables exclusively. Cross-module SQL JOINs are
prohibited. Table naming enforces this boundary visually:

| Module    | Table prefix  | Tables                                                        |
|-----------|---------------|---------------------------------------------------------------|
| Catalog   | `catalog_`    | `catalog_products`, `catalog_categories`, `catalog_stores`    |
| Inventory | `inventory_`  | `inventory_stock`                                             |
| Cart      | `cart_`       | `cart_items`                                                  |
| Order     | `order_`      | `order_orders`, `order_items`, `order_outbox_events`          |

### 2. Flyway for Controlled Schema Evolution

Schema changes are versioned migration scripts under `db/migration/`:

```
V1__initial_schema.sql    ← DDL for all module tables
V2__seed_catalog_data.sql ← Products, categories, stores seed data
V3__seed_inventory_stock.sql ← Initial stock levels (10 units/SKU)
```

`spring.jpa.hibernate.ddl-auto=validate` — Hibernate validates the schema
but never modifies it. Flyway is the single source of truth for DDL.

### 3. Native PostgreSQL Types

| Concern          | Type         | Rationale                                  |
|------------------|--------------|--------------------------------------------|
| Identifiers      | `UUID`       | Globally unique; no coordination needed    |
| Monetary amounts | `NUMERIC(12,2)` | Exact decimal arithmetic                |
| Timestamps       | `TIMESTAMPTZ` | Timezone-aware; avoids UTC drift           |
| Event payloads   | `JSONB`      | Indexed, binary JSON for outbox events     |

### 4. Outbox Pattern for Cross-Module Consistency

`order_outbox_events` is written in the **same database transaction** as
`order_orders` and `order_items`. This guarantees that domain events are
never lost even if the application crashes after the order commit. A separate
process (or scheduled job) can relay unprocessed outbox rows to downstream
modules without distributed two-phase commits.

### 5. HikariCP Connection Pool

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

Pool size is tuned conservatively for a single-instance deployment. Increase
`maximum-pool-size` linearly as horizontal replicas are added.

### 6. Performance — Indexes and N+1 Detection

All high-selectivity `WHERE` columns carry explicit B-tree indexes (defined in
`V1__initial_schema.sql`). The `dev` Spring profile enables Hibernate SQL
logging at `DEBUG` to surface N+1 problems during development.

## Consequences

**Positive:**
- Data survives restarts and deployments.
- Schema evolution is audited, reproducible, and rollback-safe via Flyway.
- Native JSONB enables indexed query on outbox payload.
- Module boundary is enforced by the naming convention and future ArchUnit rules.
- TIMESTAMPTZ prevents timezone-related bugs across geographies.

**Negative:**
- Requires a running PostgreSQL instance for local development
  (provided via `docker-compose.yml`).
- Integration tests need a PostgreSQL-compatible strategy
  (currently H2 in `MODE=PostgreSQL` with Flyway disabled for unit tests;
  Testcontainers recommended for full integration tests).
- More setup than H2 for first-time contributors.

## Related

- [ADR-0001 — Single Spring Boot Service](0001-single-spring-boot-service.md)
- [ADR-0002 — PostgreSQL with Flyway](0002-postgresql-migration.md)
