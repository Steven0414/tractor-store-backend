# Deployment Guide — Backend

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

# Run container
docker run -p 8080:8080 tractor-store/catalog-service:latest
```

## Dockerfile Overview

The service uses a two-stage build (if a Dockerfile is present) or a single
stage if built from the fat JAR:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/catalog-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

> If a multi-stage build is preferred, add a `maven:3.9-eclipse-temurin-17`
> builder stage to compile the JAR inside Docker.

## Environment Variables

Override any `application.properties` key at runtime:

```bash
docker run -p 8080:8080 \
  -e SERVER_PORT=8080 \
  tractor-store/catalog-service:latest
```

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
| H2 console (dev) | 8080/h2-console |
