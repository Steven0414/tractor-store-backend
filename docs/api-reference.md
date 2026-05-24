# API Reference

The backend runs as a single modular monolith at `http://localhost:8080` and
returns `application/json`.

## Error format

Errors are returned as Spring `ProblemDetail` payloads.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Cannot place order with empty cart"
}
```

## Catalog

### `GET /api/catalog/home`
Returns a list of featured products.

### `GET /api/catalog/categories/{filter}`
Returns products by category.

### `GET /api/catalog/products/{id}`
Returns product detail by numeric ID.

### `GET /api/catalog/recommendations?skus={csv}`
Returns category-based recommendations using the provided SKUs.

### `GET /api/catalog/stores`
Returns available pickup stores.

## Inventory

### `GET /api/inventory/{sku}`
Returns stock for a SKU.

**Response example**
```json
{ "sku": "TR-001", "available": 12 }
```

## Cart

All cart endpoints are session-aware via HttpOnly cookie `TRACTOR_CART_SESSION`.

### `GET /api/cart`
Returns full cart.

### `GET /api/cart/mini`
Returns light cart summary.

### `POST /api/cart/items`
Adds/replaces an item in the current cart.

**Request body**
```json
{ "sku": "TR-001", "quantity": 1, "unitPrice": 32000.00 }
```

### `DELETE /api/cart/items/{sku}`
Removes an item from the current cart.

### `POST /api/cart/checkout`
Requests checkout and publishes `CheckoutRequested` event.

**Request body**
```json
{
  "pickupStoreCode": "BOG-01",
  "buyerName": "Juan Perez",
  "buyerEmail": "juan@example.com"
}
```

## Order

### `POST /api/orders`
Places an order from the current session cart.

**Request body**
```json
{
  "pickupStoreCode": "BOG-01",
  "buyerName": "Juan Perez",
  "buyerEmail": "juan@example.com"
}
```

### `GET /api/orders/{id}`
Returns order detail by numeric ID.

## Internal event choreography

- `order` publishes `OrderPlaced` after commit (`@TransactionalEventListener(AFTER_COMMIT)`).
- `inventory` deducts stock.
- `cart` clears the cart.
- `notifications` simulates email notification.
