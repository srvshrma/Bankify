# Banking Java 8 Demo

Spring Boot banking API built on Java 8 with:

- REST controllers for customers, accounts, dashboard, transfers, and a Java 8 showcase endpoint
- Spring Data JPA with H2 in-memory database
- Java 8 features deliberately used across the codebase: lambdas, streams, method references, `Optional`,
  functional interfaces, default/static interface methods, `CompletableFuture`, `Base64`, and the `java.time` API
- A structure that is straightforward to migrate to Java 11 later for a feature-by-feature comparison

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## Endpoints

- `GET /api/customers`
- `POST /api/customers`
- `GET /api/accounts`
- `POST /api/accounts`
- `GET /api/accounts/{accountNumber}`
- `GET /api/accounts/dashboard/summary`
- `POST /api/transfers`
- `GET /api/showcase/java8`
