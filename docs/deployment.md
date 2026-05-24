# Deployment Guide — Backend

## Prerequisites

| Tool | Notes |
|---|---|
| PostgreSQL 16+ | Must exist before first deploy; Flyway handles schema |
| Docker 24+ | For containerised deployment |

## Building

### Maven (all services)

```bash
for svc in catalog-service inventory-service cart-service order-service notifications-service; do
  (cd $svc && ./mvnw clean package -DskipTests)
done
```

### Docker (all services)

```bash
for svc in catalog-service inventory-service cart-service order-service notifications-service; do
  docker build -t tractor-store/$svc:latest ./$svc
done
```

## Docker Compose (recommended for local development)

```bash
cd tractor-store-backend
cp .env.example .env   # set credentials if needed
docker compose up
```

## Running Individual Containers

```bash
# catalog-service (must run first — owns Flyway migrations)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/tractordb \
  -e SPRING_DATASOURCE_USERNAME=tractoruser \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  tractor-store/catalog-service:latest

# inventory-service
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/tractordb \
  -e SPRING_DATASOURCE_USERNAME=tractoruser \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  tractor-store/inventory-service:latest

# Similar for cart-service (:8082) and order-service (:8083)

# notifications-service (no DB)
docker run -p 8084:8084 tractor-store/notifications-service:latest
```

## Dockerfile Overview

All services follow the same pattern:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/<service>-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Schema Management

Flyway runs automatically on `catalog-service` startup:

```
catalog-service/src/main/resources/db/migration/
  V1__initial_schema.sql       ← All module tables (catalog_*, inventory_*, cart_*, order_*)
  V2__seed_catalog_data.sql    ← Products, categories, stores
  V3__seed_inventory_stock.sql ← Initial inventory (10 units/SKU)
```

> Always start `catalog-service` before other services in production.

## Environment Variables

| Variable | Default | Used by |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tractordb` | All DB services |
| `SPRING_DATASOURCE_USERNAME` | `tractoruser` | All DB services |
| `SPRING_DATASOURCE_PASSWORD` | `tractorpass` | All DB services |
| `SPRING_PROFILES_ACTIVE` | *(none)* | Set to `dev` for SQL logging |
| `ORDER_EVENTS_ORDER-PLACED_SUBSCRIBERS` | *(see app.properties)* | order-service only |

## Event Subscriber Configuration (order-service)

```properties
# application.properties — Docker/production URLs
order.events.order-placed.subscribers=\
  http://inventory-service:8081/internal/events/order-placed,\
  http://cart-service:8082/internal/events/order-placed,\
  http://notifications-service:8084/internal/events/order-placed
```

## Port Summary

| Service | Default port |
|---|---|
| catalog-service | 8080 |
| inventory-service | 8081 |
| cart-service | 8082 |
| order-service | 8083 |
| notifications-service | 8084 |
| PostgreSQL | 5432 (host: 54320) |
