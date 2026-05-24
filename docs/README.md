# Tractor Store — Backend Documentation

Welcome to the **Tractor Store Backend** docs. This documentation follows the
[Docs as Code](https://www.writethedocs.org/guide/docs-as-code/) methodology:
all content is written in Markdown, versioned alongside the source code, and
reviewed through pull requests.

## Table of Contents

| Document | Description |
|---|---|
| [Architecture](architecture.md) | Backend architecture and component overview |
| [API Reference](api-reference.md) | REST API endpoints for all controllers |
| [Catalog Service](catalog-service.md) | Internal service design and data model |
| [Database](database.md) | PostgreSQL schema, tables, indexes and migrations |
| [Development Guide](development.md) | Local setup and development workflow |
| [Deployment Guide](deployment.md) | Maven build and Docker deployment |
| [ADR Index](adr/README.md) | Architecture Decision Records |
| [ADR-0003](adr/0003-microservices-decomposition.md) | Decomposition into Microservices |

## Project Overview

The Tractor Store backend is composed of **5 independent Spring Boot 3**
microservices, each exposing its own REST API and running on a dedicated port.
All services share a single PostgreSQL database (`tractordb`); schema ownership
and Flyway migrations belong exclusively to `catalog-service`.

```
tractor-store-backend/
├── docker-compose.yml            # PostgreSQL + all 5 services
├── .env.example                  # Template for DB credentials
├── catalog-service/              # :8080 — Catalog, categories, stores, recommendations
├── inventory-service/            # :8081 — Stock levels per SKU
├── cart-service/                 # :8082 — Session cart management
├── order-service/                # :8083 — Order placement and confirmation
└── notifications-service/        # :8084 — Email simulation (event demo, no DB)
    Each service:
    ├── src/main/java/com/tractorstore/<module>/
    │   ├── controller/           # REST controllers
    │   ├── service/              # Business logic
    │   ├── model/                # Domain models & JPA entities
    │   ├── repository/           # JPA repositories (not in notifications-service)
    │   ├── event/                # Domain events (order-service only)
    │   └── config/               # CORS and beans config
    ├── pom.xml
    └── Dockerfile
```

## Contributing to Docs

1. Edit or create Markdown files inside `docs/`.
2. Open a pull request — the same review process applies to docs as to code.
3. Use [ADRs](adr/README.md) to record significant architectural decisions.
