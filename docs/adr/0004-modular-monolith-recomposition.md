# ADR-0004 — Recomposition to Modular Monolith

## Status

Accepted

## Context

The previous architecture decomposed the backend into 5 deployable services
(Catalog, Inventory, Cart, Order, Notifications). While this gave deployment
independence, it also introduced substantial operational complexity for the
current team and stage of the product:

- Local development and CI pipelines had to orchestrate multiple processes.
- Choreography over HTTP added avoidable latency and failure handling overhead.
- Core business invariants (checkout, order placement, stock deduction) needed
  consistency guarantees that are simpler to enforce in a single transaction
  boundary.

The product still requires strict DDD boundaries, event-driven collaboration,
and isolation guarantees to avoid architectural erosion.

## Decision

Adopt a **single Spring Boot modular monolith** in module `tractor-store/` with
DDD bounded contexts implemented as internal application modules:

- `catalog`
- `inventory`
- `cart`
- `order`
- `notifications`

### Architectural style

- Domain-Driven Design for module boundaries and ubiquitous language.
- Clean Architecture layering inside each module (`api`, `application`,
  `domain`, `infrastructure`).
- Event-driven communication inside the monolith via domain events.

### Module contracts

`catalog`
- Exposes:
  - `GET /api/catalog/home`
  - `GET /api/catalog/categories/{filter}`
  - `GET /api/catalog/products/{id}`
  - `GET /api/catalog/recommendations?skus={csv}`
  - `GET /api/catalog/stores`
- Data prefix: `catalog_*`.

`inventory`
- Exposes: `GET /api/inventory/{sku}`.
- Subscribes to `OrderPlaced` to deduct stock.
- Data prefix: `inventory_*`.

`cart`
- Exposes:
  - `GET /api/cart`
  - `GET /api/cart/mini`
  - `POST /api/cart/items`
  - `DELETE /api/cart/items/{sku}`
- Session managed by HttpOnly cookie.
- Publishes `CheckoutRequested` (when checkout is requested).
- Subscribes to `OrderPlaced` to clear cart.
- Data prefix: `cart_*`.

`order`
- Exposes:
  - `POST /api/orders`
  - `GET /api/orders/{id}`
- Handles checkout and is the canonical publisher of `OrderPlaced`.
- Implements Outbox pattern (`order_outbox_events`).
- Uses `@TransactionalEventListener(AFTER_COMMIT)` to publish `OrderPlaced`
  only after successful DB commit.
- Data prefix: `order_*`.

`notifications`
- In-memory architectural demo subscriber to `OrderPlaced`.
- Simulates e-mail sending without coupling to order module internals.

### Quality gates

- Spring Modulith verification (`ApplicationModules.verify()`).
- ArchUnit rule preventing access to `..internal..` packages from other modules.

## Consequences

### Positive

- Simpler operations (single process and deployment unit).
- Strong consistency for core business flow within one transactional boundary.
- Lower integration latency while preserving explicit domain boundaries.
- New event subscribers can be added without changing `order` internals.

### Negative

- Loss of independent runtime scaling per bounded context.
- Requires discipline and automated architecture checks to prevent a
  "big ball of mud".
- A failure in the single runtime can affect all modules.
