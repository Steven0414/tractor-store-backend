# Architecture — Backend

## Overview

The Tractor Store backend is a single **Spring Boot 3** application
(`catalog-service`) that exposes a REST/JSON API. It uses an **in-memory H2
database** seeded at startup, which keeps the service self-contained for
development and testing.

```
┌────────────────────────────────────────────────────────────┐
│                    catalog-service  (:8080)                │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │  Catalog     │  │  Cart        │  │  Order          │  │
│  │  Controller  │  │  Controller  │  │  Controller     │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬────────┘  │
│         │                 │                    │           │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌────────▼────────┐  │
│  │  Catalog     │  │  Cart        │  │  Order          │  │
│  │  Service     │  │  Service     │  │  Service        │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬────────┘  │
│         │                 │                    │           │
│  ┌──────▼─────────────────▼────────────────────▼────────┐  │
│  │               H2 In-Memory Database                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Supporting: ColorDistanceService · EventListeners   │  │
│  │              CartSessionRegistry                     │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

## Layers

| Layer | Package | Responsibility |
|---|---|---|
| Controller | `controller/` | HTTP routing, request/response mapping |
| Service | `service/` | Business logic, orchestration |
| Model | `model/` | Domain records and DTOs |
| Repository | `repository/` | JPA data access |
| Event | `event/` | Domain event definitions |
| Listener | `listener/` | Reacts to domain events |
| Data | `data/` | In-memory seed data |
| Config | `config/` | Cross-cutting config (CORS, etc.) |

## Key Design Choices

- **Session-based Cart:** the cart is stored in the HTTP session
  (`HttpSession`). `CartSessionRegistry` maps session IDs to `Cart` objects,
  allowing stateless controllers while keeping cart state server-side.
- **H2 in-memory database:** `spring.jpa.hibernate.ddl-auto=create-drop` means
  the schema and seed data are recreated on every restart. This is intentional
  for the challenge scope.
- **Color-based recommendations:** `ColorDistanceService` computes Euclidean RGB
  distance between product colors to return the 6 most visually similar
  products.
- **CORS:** `WebConfig` enables cross-origin requests from the three MFE dev
  servers.

## Related ADRs

- [ADR-0001 — Single Spring Boot service](adr/0001-single-spring-boot-service.md)
- [ADR-0002 — H2 in-memory database](adr/0002-h2-in-memory-database.md)
