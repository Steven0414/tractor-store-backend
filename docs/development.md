# Development Guide — Backend

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21 |
| Maven | ≥ 3.9 (or use the `mvnw` wrapper) |
| Docker | 24+ (required for local PostgreSQL) |

## Repository Structure

```
tractor-store-backend/
├── docker-compose.yml                  # PostgreSQL + modular monolith
├── .env.example                        # Template for DB credentials
└── tractor-store/                      # Spring Boot modular monolith (:8080)
    └── src/main/java/com/tractorstore/
        ├── catalog/
        ├── inventory/
        ├── cart/
        ├── order/
        ├── notifications/
        └── shared/events/
```

## Running Locally

### Option A — Docker Compose (recommended)

```bash
cd tractor-store-backend
cp .env.example .env
docker compose up
```

This starts PostgreSQL and the `tractor-store` monolith. Flyway runs on monolith startup.

### Option B — Maven + external PostgreSQL

```bash
# 1) Start PostgreSQL only
docker compose up postgres -d

# 2) Start monolith (runs Flyway migrations)
cd tractor-store
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Running Tests

```bash
cd tractor-store
./mvnw test --no-transfer-progress
```

Tests run with H2 in-memory and Flyway disabled (`application-test.properties`).

## Configuration

| Application Property | Env Variable | Default |
|---|---|---|
| `spring.datasource.url` | `DB_URL` | `jdbc:postgresql://localhost:5432/tractordb` |
| `spring.datasource.username` | `DB_USER` | `postgres` |
| `spring.datasource.password` | `DB_PASSWORD` | `postgres` |
| `server.port` | `PORT` | `8080` |

## Verifying the API

```bash
# Catalog — home data
curl http://localhost:8080/api/catalog/home | jq .

# Catalog — product by id
curl http://localhost:8080/api/catalog/products/1 | jq .

# Inventory — stock check
curl http://localhost:8080/api/inventory/TR-001 | jq .

# Cart — add item
curl -c cookies.txt -X POST http://localhost:8082/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"sku":"TR-001","quantity":1,"unitPrice":32000}' | jq .

# Cart — view mini
curl -b cookies.txt http://localhost:8082/api/cart/mini | jq .

# Order — place order
curl -b cookies.txt -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "pickupStoreCode":"BOG-01",
    "buyerName":"Juan Perez",
    "buyerEmail":"j@example.com"
  }' | jq .
```

## Code Style

- Constructor injection throughout — no `@Autowired` on fields.
- DDD boundaries by module package (`catalog`, `inventory`, `cart`, `order`, `notifications`).
- Clean layering inside each module (`api`, `application`, `domain`, `infrastructure`).
- Never JOIN across module table prefixes (`catalog_*`, `inventory_*`, `cart_*`, `order_*`).
- Keep shared contracts in `shared/events` and expose module interfaces explicitly.
