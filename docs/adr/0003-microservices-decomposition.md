# ADR-0003 — Decomposition into Microservices

## Status

Accepted

## Context

The initial implementation was a single `catalog-service` (modular monolith)
that handled catalog, inventory, cart, and order concerns in one process. While
this simplified early development, several DDD-aligned concerns emerged:

- Each bounded context (Catalog, Inventory, Cart, Order, Notifications) has
  distinct data ownership, lifecycle, and scaling requirements.
- The monolith made it impossible to deploy or test a single context in
  isolation.
- Adding a new subscriber to the order-placed event required modifying the
  monolith's source code.
- The frontend microfrontend architecture (`mfe-explore`, `mfe-decide`,
  `mfe-checkout`) already separates concerns at the UI layer; the backend
  should mirror this.

## Decision

Extract each bounded context into its own **Spring Boot 3 / Java 17**
microservice. All services share a single PostgreSQL database (`tractordb`)
but own their tables exclusively (module-prefixed: `catalog_*`, `inventory_*`,
`cart_*`, `order_*`).

| Service | Port | Bounded Context |
|---|---|---|
| `catalog-service` | 8080 | Catalog, categories, stores, recommendations |
| `inventory-service` | 8081 | Stock levels per SKU |
| `cart-service` | 8082 | Session cart management |
| `order-service` | 8083 | Order placement and confirmation |
| `notifications-service` | 8084 | Email notification simulation |

**Schema ownership:** `catalog-service` is the sole Flyway runner and creates
all tables. Other services set `spring.flyway.enabled=false`.

**Inter-service communication:** HTTP choreography replaces in-process Spring
events. After an order commits, `order-service` calls
`POST /internal/events/order-placed` on each subscriber. Subscriber URLs are
configured via `order.events.order-placed.subscribers` — a new subscriber can
be added without changing `order-service` source code.

**Reliability:** `@TransactionalEventListener(AFTER_COMMIT)` in `order-service`
ensures the HTTP calls only happen after the database transaction is truly
committed. The Outbox pattern (`order_outbox_events`) is retained as a
consistency safety net. Subscriber failures are logged but do not fail the
order response (best-effort delivery).

## Consequences

**Positive:**
- Each service can be deployed, scaled, and tested independently.
- Adding a new event subscriber (e.g. an SMS service) requires zero code changes
  — only a config update to `order.events.order-placed.subscribers`.
- `notifications-service` demonstrates the open/closed principle in a
  distributed context: the system is open for extension without modifying
  existing services.
- Domain boundaries are enforced at the network level, making accidental
  cross-module coupling impossible.

**Negative:**
- Local development requires starting up to 5 processes (mitigated by
  Docker Compose).
- Inter-service HTTP calls introduce network latency and partial-failure
  scenarios (mitigated by best-effort delivery + Outbox).
- Shared database means a slow migration or schema lock in one module can
  affect others (future mitigation: dedicated database per service).
- Distributed tracing and observability require additional tooling (not yet
  implemented).
