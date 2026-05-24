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

## Project Overview

The Tractor Store backend consists of a single **Spring Boot 3** service
(`catalog-service`) that exposes REST APIs consumed by the frontend
microfrontends.

```
tractor-store-backend/
└── catalog-service/          # Spring Boot 3 / Java 17 service (port 8080)
    └── src/main/java/com/tractorstore/
        ├── controller/       # REST controllers
        ├── service/          # Business logic
        ├── model/            # Domain models & DTOs
        ├── repository/       # JPA repositories
        ├── event/            # Domain events
        ├── listener/         # Event listeners
        └── data/             # In-memory seed data
```

## Contributing to Docs

1. Edit or create Markdown files inside `docs/`.
2. Open a pull request — the same review process applies to docs as to code.
3. Use [ADRs](adr/README.md) to record significant architectural decisions.
