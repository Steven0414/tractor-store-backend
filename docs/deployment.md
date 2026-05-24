# Deployment Guide — Backend

## Prerequisites

| Tool | Notes |
|---|---|
| PostgreSQL 16+ | Must exist before first deploy; Flyway handles schema |
| Docker 24+ | For containerised deployment |

## Building

### Maven (JAR)

```bash
cd catalog-service
./mvnw clean package -DskipTests
java -jar target/catalog-service-1.0.0.jar
```

### Docker

```bash
# Build image
docker build -t tractor-store/catalog-service:latest ./catalog-service

# Run container (point to an existing PostgreSQL instance)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/tractordb \
  -e SPRING_DATASOURCE_USERNAME=tractoruser \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  tractor-store/catalog-service:latest
```

### Docker Compose (local development)

```bash
cd tractor-store-backend
cp .env.example .env   # set credentials
docker compose up
```

## Dockerfile Overview

Two-stage build: Maven compiles the JAR, then a minimal JRE image runs it.

```dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
# ... compile + package

FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Schema Management

Flyway runs automatically on startup and applies any pending migrations:

```
db/migration/
  V1__initial_schema.sql     ← All module tables (catalog_, inventory_, cart_, order_)
  V2__seed_catalog_data.sql  ← Products, categories, stores
  V3__seed_inventory_stock.sql ← Initial inventory (10 units/SKU)
```

**Never** set `spring.jpa.hibernate.ddl-auto=update` or `create-drop` in
production. The production default is `validate`.

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tractordb` | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | `tractoruser` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | `tractorpass` | DB password |
| `SPRING_PROFILES_ACTIVE` | *(none)* | Set to `dev` for SQL logging |

## CORS in Production

Update `WebConfig` to restrict allowed origins to the actual frontend domains
before deploying to production:

```java
registry.addMapping("/api/**")
    .allowedOrigins("https://explore.tractorstore.com",
                    "https://decide.tractorstore.com",
                    "https://checkout.tractorstore.com");
```

## Health Check

```bash
curl -f http://localhost:8080/actuator/health
```

> Add `spring-boot-starter-actuator` to `pom.xml` to enable `/actuator/health`.

## Port Summary

| Service | Default port |
|---|---|
| catalog-service | 8080 |
| PostgreSQL | 5432 |

