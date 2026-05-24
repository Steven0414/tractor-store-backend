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
├── docker-compose.yml                  # PostgreSQL + catalog-service
├── .env.example                        # Template for DB credentials
└── catalog-service/
    ├── src/
    │   ├── main/java/com/tractorstore/   # Application source
    │   │   ├── model/catalog/            # catalog_* JPA entities
    │   │   ├── model/inventory/          # inventory_* JPA entities
    │   │   ├── model/cart/               # cart_* JPA entities
    │   │   ├── model/order/              # order_* JPA entities + DTOs
    │   │   └── model/outbox/             # OutboxEvent entity
    │   ├── main/resources/
    │   │   ├── application.properties    # PostgreSQL + Flyway + HikariCP
    │   │   ├── application-dev.properties # SQL logging (N+1 detection)
    │   │   └── db/migration/             # Flyway scripts (V1, V2, V3)
    │   └── test/
    │       ├── java/com/tractorstore/    # Tests
    │       └── resources/
    │           └── application-test.properties  # H2 for unit tests
    ├── pom.xml
    └── Dockerfile
```

## Running Locally

### Option A — Full Docker Compose (recommended)

```bash
cd tractor-store-backend

# Copy and optionally edit credentials
cp .env.example .env

# Start PostgreSQL + catalog-service (with dev profile = SQL logging)
docker compose up
```

### Option B — Maven + external PostgreSQL

```bash
# Start only PostgreSQL
docker compose up postgres -d

# Run the service with the dev profile
cd catalog-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The service starts on **http://localhost:8080**.

Flyway runs automatically on startup and applies pending migrations
(`V1__initial_schema.sql`, `V2__seed_catalog_data.sql`, `V3__seed_inventory_stock.sql`).

## Running Tests

```bash
cd catalog-service
./mvnw test
```

Tests use the `test` profile (H2 in-memory, Flyway disabled, `ddl-auto=create-drop`).

## Detecting N+1 Queries

Activate the `dev` profile to enable Hibernate SQL logging:

```properties
# application-dev.properties (already configured)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

Use `EXPLAIN ANALYZE` in PostgreSQL to measure query plans:

```sql
EXPLAIN ANALYZE SELECT * FROM catalog_products WHERE category = 'classic';
```

## Configuration

All runtime settings are in `src/main/resources/application.properties`.
Override with environment variables or create a profile:

| Property | Env Variable | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tractordb` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `tractoruser` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `tractorpass` |

## Verifying the API

```bash
# Home data
curl http://localhost:8080/api/catalog/home | jq .

# Products by category
curl http://localhost:8080/api/catalog/categories/classic | jq .

# Stores
curl http://localhost:8080/api/catalog/stores | jq .

# Mini cart
curl -c cookies.txt http://localhost:8080/api/cart/mini | jq .

# Add to cart
curl -b cookies.txt -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"sku":"TRK-001","name":"Classic Tractor","quantity":1,"price":12500}' | jq .

# Place order
curl -b cookies.txt -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Juan","lastName":"Pérez","email":"j@example.com",
    "phone":"123","address":"Calle 1","city":"Bogotá","postalCode":"110111",
    "paymentMethod":"card",
    "items":[{"sku":"TRK-001","name":"Classic Tractor","quantity":1,"price":12500}]
  }' | jq .
```

## Code Style

- Use Java records for immutable DTOs/models.
- Keep JPA entities in `model/<module>/` sub-packages.
- Keep business logic in services, not controllers.
- Never JOIN across module table prefixes (`catalog_*`, `inventory_*`, `cart_*`, `order_*`).
- Register new domain events in `event/` and handle them in `listener/`.

