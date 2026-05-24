# Architecture Decision Records — Backend

An **Architecture Decision Record (ADR)** captures a significant architectural
decision, its context, and its consequences.

## Index

| ADR | Title | Status |
|---|---|---|
| [0001](0001-single-spring-boot-service.md) | Single Spring Boot Service | Superseded |
| [0002](0002-postgresql-migration.md) | PostgreSQL with Flyway | Accepted |
| [0003](0003-microservices-decomposition.md) | Decomposition into Microservices | Superseded |
| [0004](0004-modular-monolith-recomposition.md) | Recomposition to Modular Monolith | Accepted |

## Format

```
# ADR-XXXX — Title

## Status
Proposed | Accepted | Deprecated | Superseded by ADR-YYYY

## Context
## Decision
## Consequences
```

## Creating a New ADR

1. Copy an existing ADR as a template.
2. Name the file `NNNN-short-title.md`.
3. Set status to `Proposed` and open a pull request.
4. Update this index once merged.
