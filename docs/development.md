# Development Guide — Backend

## Prerequisites

| Tool | Version |
|---|---|
| Java | 17 |
| Maven | ≥ 3.9 (or use the `mvnw` wrapper) |
| Docker | 24+ (optional, for containerised runs) |

## Repository Structure

```
tractor-store-backend/
└── catalog-service/
    ├── src/
    │   ├── main/java/com/tractorstore/   # Application source
    │   └── test/java/com/tractorstore/   # Tests
    ├── src/main/resources/
    │   └── application.properties        # Runtime configuration
    ├── pom.xml
    └── Dockerfile
```

## Running Locally

```bash
cd catalog-service
./mvnw spring-boot:run
```

The service starts on **http://localhost:8080**.

> The H2 in-memory database is seeded automatically at startup via `CatalogData`.
> No external database setup is required.

## Running Tests

```bash
cd catalog-service
./mvnw test
```

## H2 Console

While the service is running, open **http://localhost:8080/h2-console** in your
browser to inspect the database.

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:tractordb` |
| Username | `sa` |
| Password | *(leave blank)* |

## Configuration

All runtime settings are in `src/main/resources/application.properties`.
For local overrides, create `application-local.properties` and activate it with:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

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
  -d '{"sku":"TRK-001","name":"Classic Tractor","quantity":1,"price":15000}' | jq .
```

## Code Style

- Use Java records for immutable DTOs/models.
- Keep business logic in services, not controllers.
- Register new domain events in `event/` and handle them in `listener/`.
