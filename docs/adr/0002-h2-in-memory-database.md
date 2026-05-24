# ADR-0002 — H2 In-Memory Database

## Status

Accepted

## Context

The Tractor Store challenge requires a working backend with persistent-enough
data to demo the full purchase flow. Setting up a production database (PostgreSQL,
MySQL) would require external infrastructure and increase the setup friction for
reviewers running the project locally.

## Decision

Use **H2 in-memory database** with `spring.jpa.hibernate.ddl-auto=create-drop`.
Seed data is loaded from `CatalogData` at application startup. The H2 web
console is enabled for development convenience.

## Consequences

**Positive:**
- Zero external dependencies — the application is fully self-contained.
- Fast startup and test execution.
- H2 console enables quick data inspection without external tooling.

**Negative:**
- All data (cart state, orders, seed data) is lost on restart.
- Not suitable for production without replacing with a persistent database
  (e.g., PostgreSQL via `spring.datasource.url=jdbc:postgresql://...`).
- H2 SQL dialect differences may mask issues that would appear with a
  production database.

## Migration Path

To switch to PostgreSQL:
1. Replace `com.h2database:h2` with `org.postgresql:postgresql` in `pom.xml`.
2. Update `spring.datasource.*` properties.
3. Set `spring.jpa.hibernate.ddl-auto=validate` and manage schema with Flyway
   or Liquibase.
4. Move seed data to a Flyway migration script.
