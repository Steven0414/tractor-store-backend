# Catalog Service — Internal Design

## Overview

`catalog-service` is the single backend service of the Tractor Store. It is a
**Spring Boot 3 / Java 17** application that exposes catalog, cart, and order
REST APIs, backed by an **H2 in-memory database**.

## Domain Model

```
Product          — a single variant SKU (id, sku, name, price, imageUrl,
                   category, color, motor)
Category         — product grouping (id, name, filter, imageUrl, description)
Store            — physical store location
Cart             — session-scoped list of CartItems
CartItem         — (sku, name, quantity, price, subtotal)
MiniCart         — lightweight (itemCount, total)
HomeData         — aggregated home page payload
PlaceOrderRequest — customer info + shipping address
PlaceOrderResponse — confirmation (orderId, message, total)
OutboxEvent      — persisted domain event for async processing
```

## Services

### `CatalogService`
- Reads products, categories, stores, and home data from `CatalogData`
  (in-memory seed).
- Delegates color-similarity computation to `ColorDistanceService`.

### `CartService`
- Uses `CartSessionRegistry` to map `HttpSession.getId()` → `Cart`.
- Carts are stored in-process (no persistence); they survive the session but are
  lost on restart.
- `addItem()` increments quantity if the SKU already exists in the cart.

### `OrderService`
- Retrieves the session cart via `CartSessionRegistry`.
- Persists an `OutboxEvent` (via `OutboxEventRepository`) to enable future async
  processing.
- Publishes an `OrderPlacedEvent` to the Spring application context.
- Clears the session cart after a successful order.

### `ColorDistanceService`
- Parses hex color strings from product records.
- Computes Euclidean distance in RGB space between two colors.
- Used by `CatalogService.getRecommendations()` to rank products by color
  similarity.

### `InventoryService`
- Returns stock levels per SKU from the in-memory seed data.

### `CartSessionRegistry`
- Simple `ConcurrentHashMap<String, Cart>` keyed by session ID.
- Ensures thread-safe access to per-session carts.

## Event System

| Event | Publisher | Listener | Action |
|---|---|---|---|
| `OrderPlacedEvent` | `OrderService` | `CartEventListener` | Clears session cart |
| `OrderPlacedEvent` | `OrderService` | `InventoryEventListener` | Decrements stock |

## Configuration

Key settings in `src/main/resources/application.properties`:

| Property | Value | Purpose |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:tractordb` | In-memory H2 DB |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | Schema recreated on restart |
| `spring.h2.console.enabled` | `true` | H2 browser console (dev) |

## CORS

`WebConfig` registers a `CorsRegistry` that allows all origins (`*`) for
development. **Restrict this in production** to the known frontend domains.
