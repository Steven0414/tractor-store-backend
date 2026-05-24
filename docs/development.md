# Development Guide — Backend

## Prerequisites

| Tool | Version |
|---|---|
| Java | 17 |
| Maven | ≥ 3.9 (or use the `mvnw` wrapper) |
| Docker | 24+ (required for local PostgreSQL) |

## Repository Structure

```
tractor-store-backend/
├── docker-compose.yml                  # PostgreSQL + all 5 services
├── .env.example                        # Template for DB credentials
├── catalog-service/       (:8080)      # Catalog, categories, stores, recommendations
│   └── src/main/resources/db/migration/  # Flyway — owns schema for ALL services
├── inventory-service/     (:8081)      # Stock levels per SKU
├── cart-service/          (:8082)      # Session cart management
├── order-service/         (:8083)      # Order placement and confirmation
└── notifications-service/ (:8084)      # Email simulation (no DB)
    Each service follows:
    src/main/java/com/tractorstore/<module>/
    ├── controller/         # REST endpoints
    ├── service/            # Business logic
    ├── model/              # DTOs and JPA entities
    ├── repository/         # JPA repositories
    └── config/             # CORS, beans
```

## Running Locally

### Option A — Full Docker Compose (recommended)

```bash
cd tractor-store-backend
cp .env.example .env
docker compose up
```

This starts PostgreSQL and all 5 services. `catalog-service` runs Flyway on startup to initialize the schema.

### Option B — Maven + external PostgreSQL

```bash
# 1. Start PostgreSQL only
docker compose up postgres -d

# 2. Start catalog-service first — it runs Flyway migrations
cd catalog-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
cd ..

# 3. Start remaining services (any order)
cd inventory-service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &
cd ../cart-service    && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &
cd ../order-service   && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &
cd ../notifications-service && ./mvnw spring-boot:run &
```

> ⚠️ **catalog-service must start before other services** when running without Docker Compose, because it runs the Flyway migrations that create the database schema.

## Running Tests

```bash
# Run tests for all services
for svc in catalog-service inventory-service cart-service order-service notifications-service; do
  echo "=== $svc ===" && (cd $svc && ./mvnw test -q)
done
```

All services use the `test` profile: H2 in-memory, Flyway disabled, `ddl-auto=create-drop`.

## Configuration

| Property | Env Variable | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tractordb` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `tractoruser` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `tractorpass` |

## Verifying the API

```bash
# Catalog — home data
curl http://localhost:8080/api/catalog/home | jq .

# Catalog — product by SKU
curl http://localhost:8080/api/catalog/products/TRK-001 | jq .

# Inventory — stock check
curl http://localhost:8081/api/inventory/TRK-001 | jq .

# Cart — add item
curl -c cookies.txt -X POST http://localhost:8082/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"sku":"TRK-001","name":"Tractor Rojo Clásico","quantity":1,"price":12500}' | jq .

# Cart — view mini
curl -b cookies.txt http://localhost:8082/api/cart/mini | jq .

# Order — place order
curl -b cookies.txt -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Juan","lastName":"Pérez","email":"j@example.com",
    "phone":"123","address":"Calle 1","city":"Bogotá","postalCode":"110111",
    "paymentMethod":"CARD",
    "items":[{"sku":"TRK-001","name":"Tractor Rojo Clásico","quantity":1,"price":12500}]
  }' | jq .
```

## Code Style

- Constructor injection throughout — no `@Autowired` on fields.
- JPA entities in `model/` sub-packages; immutable DTOs as Java records where possible.
- Business logic in services, not controllers.
- Never JOIN across module table prefixes (`catalog_*`, `inventory_*`, `cart_*`, `order_*`).
- Add new event subscribers by updating `order.events.order-placed.subscribers` in config — no code changes needed.
