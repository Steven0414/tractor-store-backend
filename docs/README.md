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
| [Database](database.md) | PostgreSQL schema, tables, indexes and migrations |
| [Development Guide](development.md) | Local setup and development workflow |
| [Deployment Guide](deployment.md) | Maven build and Docker deployment |
| [ADR Index](adr/README.md) | Architecture Decision Records |
| [ADR-0004](adr/0004-modular-monolith-recomposition.md) | Recomposition to Modular Monolith |

## Project Overview

The Tractor Store backend is a **single Spring Boot modular monolith** with DDD
boundaries for `catalog`, `inventory`, `cart`, `order`, and `notifications`.
It exposes a unified API at port `8080` and persists data in PostgreSQL
(`tractordb`) using Flyway-managed migrations.

```
tractor-store-backend/
├── docker-compose.yml            # PostgreSQL + monolith runtime
├── .env.example                  # Template for DB credentials
└── tractor-store/                # :8080 — Modular monolith
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/com/tractorstore/
        ├── catalog/
        ├── inventory/
        ├── cart/
        ├── order/
        ├── notifications/
        └── shared/events/
```

## Contributing to Docs

1. Edit or create Markdown files inside `docs/`.
2. Open a pull request — the same review process applies to docs as to code.
3. Use [ADRs](adr/README.md) to record significant architectural decisions.
