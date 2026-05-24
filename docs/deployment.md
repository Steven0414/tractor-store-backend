# Deployment Guide — Backend

## Prerequisites

| Tool | Notes |
|---|---|
| Java 21 | For local Maven packaging |
| PostgreSQL 16+ | Required by the backend runtime |
| Docker 24+ | For containerized deployment |

## Building

### Maven (monolith)

```bash
cd tractor-store
./mvnw clean package -DskipTests --no-transfer-progress
```

### Docker (single image)

```bash
docker build -t tractor-store/backend:latest ./tractor-store
```

## Docker Compose

```bash
cd tractor-store-backend
cp .env.example .env   # set credentials if needed
docker compose up
```

This boots `postgres` and `tractor-store`.

## Running Container Manually

```bash
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://<host>:5432/tractordb \
  -e DB_USER=tractoruser \
  -e DB_PASSWORD=<password> \
  -e PORT=8080 \
  tractor-store/backend:latest
```

## Dockerfile Overview

The backend uses a multi-stage Dockerfile in `tractor-store/Dockerfile`:

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Schema Management

Flyway runs automatically on `tractor-store` startup:

```
tractor-store/src/main/resources/db/migration/
  V1__init_modular_monolith.sql
```

## Environment Variables

| Variable | Default | Used by |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/tractordb` | `tractor-store` |
| `DB_USER` | `postgres` | `tractor-store` |
| `DB_PASSWORD` | `postgres` | `tractor-store` |
| `PORT` | `8080` | `tractor-store` |

## Port Summary

| Component | Default port |
|---|---|
| tractor-store | 8080 |
| PostgreSQL | 5432 (host: 54320) |
