# API Reference

The backend is composed of **5 independent microservices**. All endpoints
return `application/json`.

## Services

| Service | Port | Base URL |
|---|---|---|
| `catalog-service` | 8080 | `http://localhost:8080` |
| `inventory-service` | 8081 | `http://localhost:8081` |
| `cart-service` | 8082 | `http://localhost:8082` |
| `order-service` | 8083 | `http://localhost:8083` |
| `notifications-service` | 8084 | `http://localhost:8084` |

---

## Error format (all services)

All services return RFC 7807 `ProblemDetail` for errors:

```json
{
  "type": "https://tractorstore.com/errors/validation-error",
  "title": "Validation Failed",
  "status": 422,
  "detail": "email: must be a well-formed email address"
}
```

**Common status codes**

| Status | Cause |
|---|---|
| `400 Bad Request` | `IllegalArgumentException` |
| `404 Not Found` | Resource not found |
| `422 Unprocessable Entity` | Bean validation failure |
| `500 Internal Server Error` | Unexpected error |

---

## Catalog Service — `http://localhost:8080`

### `GET /api/catalog/home`

Returns banner content, featured categories, and featured products.

**Response — `200 OK`**
```json
{
  "bannerTitle": "string",
  "bannerSubtitle": "string",
  "featuredCategories": [
    { "id": "uuid", "name": "string", "filter": "string", "imageUrl": "string", "description": "string" }
  ],
  "featuredProducts": [
    { "id": "uuid", "sku": "string", "name": "string", "price": 0.0, "imageUrl": "string", "category": "string", "color": "#RRGGBB", "motor": "string" }
  ]
}
```

---

### `GET /api/catalog/categories/{filter}`

Returns products matching the given filter.

**Path params**

| Param | Type | Values |
|---|---|---|
| `filter` | string | `classic`, `autonomous`, `all` |

**Response — `200 OK`** — `Product[]`

---

### `GET /api/catalog/products/{sku}`

Returns a single product by SKU.

**Response — `200 OK`** — `Product`

**Response — `404 Not Found`** — product SKU does not exist.

---

### `GET /api/catalog/recommendations?skus={csv}`

Returns up to 6 products whose colors are most visually similar (Euclidean RGB
distance) to the provided SKUs.

**Query params**

| Param | Type | Example |
|---|---|---|
| `skus` | string (CSV) | `TRK-001,AUT-002` |

**Response — `200 OK`** — `Product[]`

---

### `GET /api/catalog/stores`

Returns all store locations.

**Response — `200 OK`**
```json
[
  {
    "id": "uuid", "name": "string", "address": "string",
    "city": "string", "phone": "string", "email": "string",
    "latitude": 0.0, "longitude": 0.0, "openingHours": "string"
  }
]
```

---

## Inventory Service — `http://localhost:8081`

### `GET /api/inventory/{sku}`

Returns current stock for a SKU.

**Response — `200 OK`**
```json
{ "sku": "TRK-001", "quantity": 10 }
```

**Response — `404 Not Found`** — SKU does not exist.

---

### `POST /internal/events/order-placed` *(internal)*

Called by `order-service` after an order is confirmed. Deducts stock for each
item in the order.

**Request body**
```json
{
  "orderId": "uuid",
  "sessionId": "string",
  "items": [{ "sku": "string", "name": "string", "quantity": 1, "price": 0.0 }]
}
```

**Response — `200 OK`**

---

## Cart Service — `http://localhost:8082`

All `/api/cart` endpoints are session-aware (cookie-based `JSESSIONID`).

### `GET /api/cart`

Returns the full cart for the current session.

**Response — `200 OK`**
```json
{
  "items": [{ "sku": "string", "name": "string", "quantity": 1, "price": 0.0 }],
  "itemCount": 1,
  "total": 0.0
}
```

---

### `GET /api/cart/mini`

Returns a lightweight cart summary.

**Response — `200 OK`**
```json
{ "itemCount": 1, "total": 0.0 }
```

---

### `POST /api/cart/items`

Adds a product to the cart or increments its quantity if it already exists.

**Request body**
```json
{ "sku": "string", "name": "string", "quantity": 1, "price": 0.0 }
```

**Response — `200 OK`** — full `Cart` object (see `GET /api/cart`)

---

### `DELETE /api/cart/items/{sku}`

Removes a specific item from the cart.

**Response — `204 No Content`**

---

### `POST /internal/events/order-placed` *(internal)*

Called by `order-service` after an order is confirmed. Clears the cart for the
given `sessionId`.

**Request body** — same as inventory-service event endpoint.

**Response — `200 OK`**

---

## Order Service — `http://localhost:8083`

### `POST /api/orders`

Places a new order. Session-aware (`JSESSIONID` used to link to the cart).

**Request body**
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "user@example.com",
  "phone": "string",
  "address": "string",
  "city": "string",
  "postalCode": "string",
  "paymentMethod": "string",
  "items": [
    { "sku": "string", "name": "string", "quantity": 1, "price": 0.0 }
  ]
}
```

**Response — `201 Created`**
```json
{ "orderId": "uuid", "status": "CONFIRMED", "total": 0.0 }
```

**Errors** — `422 Unprocessable Entity` for validation failures (missing or
invalid fields).

After commit, `order-service` fans out a `POST /internal/events/order-placed`
to all configured subscribers (inventory, cart, notifications).

---

### `GET /api/orders/{id}`

Returns order details by UUID.

**Response — `200 OK`**
```json
{
  "orderId": "uuid",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "status": "CONFIRMED",
  "total": 0.0,
  "createdAt": "2024-01-01T00:00:00Z",
  "items": [
    { "sku": "string", "name": "string", "quantity": 1, "price": 0.0 }
  ]
}
```

**Response — `404 Not Found`** — order UUID does not exist.

---

## Notifications Service — `http://localhost:8084`

### `POST /internal/events/order-placed` *(internal)*

Called by `order-service`. Logs a simulated email confirmation. No persistence.

**Request body** — same as inventory-service event endpoint.

**Response — `200 OK`**

---

## Event flow

After `POST /api/orders` commits, `order-service` calls all configured
subscribers in parallel:

```
POST /api/orders (order-service)
  └─► [AFTER_COMMIT]
        ├── POST http://inventory-service:8081/internal/events/order-placed
        ├── POST http://cart-service:8082/internal/events/order-placed
        └── POST http://notifications-service:8084/internal/events/order-placed
```

Subscriber URLs are configured in `order-service`'s `application.properties`
via `order.events.order-placed.subscribers`. Adding a new subscriber requires
no code changes to `order-service`.
