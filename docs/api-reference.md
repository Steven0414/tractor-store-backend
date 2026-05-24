# API Reference — catalog-service

Base URL: `http://localhost:8080`

All endpoints return `application/json`. Cart and order endpoints are
session-aware (cookie-based `JSESSIONID`).

---

## Catalog

### `GET /api/catalog/home`

Returns home page data: banner content, featured categories, and featured
products.

**Response — `200 OK`**
```json
{
  "bannerTitle": "string",
  "bannerSubtitle": "string",
  "featuredCategories": [
    { "id": "string", "name": "string", "filter": "string",
      "imageUrl": "string", "description": "string" }
  ],
  "featuredProducts": [
    { "id": "string", "sku": "string", "name": "string", "price": 0.0,
      "imageUrl": "string", "category": "string", "color": "string", "motor": "string" }
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

### `GET /api/catalog/stores`

Returns all store locations.

**Response — `200 OK`**
```json
[
  {
    "id": "string", "name": "string", "address": "string",
    "city": "string", "phone": "string", "email": "string",
    "latitude": 0.0, "longitude": 0.0, "openingHours": "string"
  }
]
```

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

## Cart

All cart endpoints operate on the caller's HTTP session.

### `GET /api/cart`

Returns the full cart.

**Response — `200 OK`**
```json
{
  "items": [
    { "sku": "string", "name": "string", "quantity": 1, "price": 0.0, "subtotal": 0.0 }
  ],
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

## Orders

### `POST /api/orders`

Places a new order for the current session's cart.

**Request body**
```json
{
  "customerName": "string",
  "customerEmail": "string",
  "shippingAddress": "string"
}
```

**Response — `201 Created`**
```json
{
  "orderId": "string",
  "message": "string",
  "total": 0.0
}
```

**Errors**

| Status | Cause |
|---|---|
| `400 Bad Request` | Validation failure on request body fields |
| `422 Unprocessable Entity` | Cart is empty |

---

## H2 Console (dev only)

`GET /h2-console` — browser-based SQL console for inspecting the in-memory
database. Available only when `spring.h2.console.enabled=true` (default in
`application.properties`).
