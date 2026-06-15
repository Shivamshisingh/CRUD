# CRUD Application

A basic Spring Boot CRUD REST API for managing products, with JUnit 5 unit tests and Cucumber BDD integration tests.

## Tech Stack

| Technology         | Version  | Purpose                              |
|--------------------|----------|--------------------------------------|
| Spring Boot        | 4.0.6    | Application framework                |
| Java               | 21       | Language                             |
| Spring Data JPA    | -        | Database access (ORM)                |
| H2 Database        | -        | In-memory database (dev/test)        |
| Bean Validation    | -        | Request validation                   |
| JUnit 5            | -        | Unit testing framework               |
| Mockito            | -        | Mocking for unit tests               |
| Cucumber 7.20      | -        | BDD integration testing              |
| Maven              | 3.6+     | Build tool                           |

## Project Structure

```
src/
├── main/
│   ├── java/com/example/crud/
│   │   ├── CrudApplication.java            # Spring Boot entry point
│   │   ├── model/
│   │   │   └── Product.java                # JPA entity
│   │   ├── repository/
│   │   │   └── ProductRepository.java      # Spring Data JPA repository
│   │   ├── service/
│   │   │   └── ProductService.java         # Business logic
│   │   ├── controller/
│   │   │   └── ProductController.java      # REST endpoints
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties          # App config (H2, JPA)
│
├── test/
│   ├── java/com/example/crud/
│   │   ├── CrudApplicationTests.java       # Spring context load test
│   │   ├── unit/
│   │   │   ├── service/
│   │   │   │   └── ProductServiceTest.java      # Service layer unit tests (Mockito)
│   │   │   └── controller/
│   │   │       └── ProductControllerTest.java   # Controller layer unit tests (MockMvc)
│   │   └── integration/
│   │       ├── CucumberIT.java                  # Cucumber test runner
│   │       ├── CucumberSpringConfiguration.java # Spring context for Cucumber
│   │       └── steps/
│   │           └── ProductStepDefinitions.java  # Step definitions
│   └── resources/
│       └── features/
│           └── product.feature                  # Gherkin scenarios
```

## API Endpoints

| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| GET    | `/api/products`       | Get all products     |
| GET    | `/api/products/{id}`  | Get product by ID    |
| POST   | `/api/products`       | Create a product     |
| PUT    | `/api/products/{id}`  | Update a product     |
| DELETE | `/api/products/{id}`  | Delete a product     |

### Sample Request Body (POST / PUT)

```json
{
  "name": "Laptop",
  "description": "A powerful laptop",
  "price": 999.99
}
```

## How to Run

### Prerequisites
- Java 21+
- Maven 3.6+

### Start the Application

```bash
./mvnw spring-boot:run
```

The app runs on `http://localhost:8080`. H2 console is available at `http://localhost:8080/h2-console`.

## Testing

### JUnit vs Cucumber — When to Use What?

| Aspect           | JUnit (Unit Tests)                        | Cucumber (Integration Tests)                   |
|------------------|-------------------------------------------|------------------------------------------------|
| **Scope**        | Single class/method in isolation           | Full API flow (controller → service → DB)      |
| **Speed**        | Fast (mocked dependencies)                 | Slower (boots full Spring context)             |
| **Style**        | Code-based assertions                      | Gherkin (human-readable `.feature` files)      |
| **Best for**     | Business logic, edge cases, error handling | End-to-end API validation, BDD acceptance tests|
| **Dependencies** | Mocked via Mockito                         | Real (H2 database, real HTTP calls)            |

**Recommendation:** Use **both**. JUnit tests give you fast feedback on individual components. Cucumber tests validate that the full system works end-to-end. They complement each other.

### Run Unit Tests Only (JUnit + Mockito)

```bash
./mvnw test
```

This runs all `*Test.java` files via the **Surefire** plugin.

### Run Integration Tests Only (Cucumber)

```bash
./mvnw failsafe:integration-test
```

This runs all `*IT.java` files (the Cucumber runner) via the **Failsafe** plugin.

### Run All Tests (Unit + Integration)

```bash
./mvnw verify
```

This runs unit tests first (`test` phase), then integration tests (`integration-test` phase).

### Cucumber Reports

After running Cucumber tests, an HTML report is generated at:

```
target/cucumber-reports/cucumber.html
```

## Dependencies (pom.xml)

### Runtime
- `spring-boot-starter-webmvc` — REST controllers
- `spring-boot-starter-data-jpa` — JPA / Hibernate
- `spring-boot-starter-validation` — Bean Validation
- `spring-boot-h2console` — H2 web console
- `h2` — In-memory database

### Test
- `spring-boot-starter-webmvc-test` — MockMvc, test utilities
- `spring-boot-starter-data-jpa-test` — JPA test utilities
- `cucumber-java` — Cucumber step definitions
- `cucumber-spring` — Cucumber + Spring integration
- `cucumber-junit-platform-engine` — Run Cucumber with JUnit 5
- `junit-platform-suite` — `@Suite` runner for Cucumber
