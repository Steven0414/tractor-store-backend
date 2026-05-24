# ADR-0001 — Single Spring Boot Service

## Status

Accepted

## Context

The Tractor Store challenge involves three user journeys (explore, decide,
checkout) that all share the same product catalog and session cart. Splitting
these into separate microservices would introduce inter-service communication
overhead and distributed session management complexity that is not justified
at this scale.

## Decision

Implement a single **Spring Boot 3** service (`catalog-service`) that handles
catalog, cart, and order concerns. Domain boundaries are enforced at the package
and service layer level rather than at the network level.

## Consequences

**Positive:**
- Simple deployment (single JAR / single Docker image).
- No inter-service latency or distributed tracing required.
- Shared in-process `CartSessionRegistry` keeps session state trivial.
- Easy local development — one process to start.

**Negative:**
- All concerns scale together; the catalog cannot be scaled independently of
  the cart/order logic.
- A bug in one area (e.g., order placement) could affect the entire service.
- Not suitable for a high-traffic production system without decomposition.
