# Product Service

The **Product Service** is a Spring Boot microservice responsible for managing the product catalog, customer reviews, ratings, search capabilities, and analytics calculations for both sellers and administrators. It uses polyglot persistence, storing catalog data in **MongoDB** for high-performance retrieval and mapping analytics data in **MySQL**.

---

## Features

*   **Product Catalog Management**: Fully featured CRUD operations for product catalogs, including multipart file uploads for product images.
*   **Search and Filtering**: Specialized search endpoints supporting product filtering by categories, brands, price range, stock levels, and search strings.
*   **Inventory Synchronization**: Endpoint bindings allowing other microservices (such as the Order Service) to increase or reduce product stock levels atomically.
*   **Ratings and Reviews**: Review sub-system managing customer product reviews, delete capability, and dynamic average rating calculations.
*   **Analytics Engines**:
    *   **Seller Analytics**: Provides sellers with analytics on their items, stock levels, and orders.
    *   **Admin Analytics**: Provides overall system catalog statistics, user distribution, and product metrics.

---

## Tech Stack

*   **Core**: Spring Boot 3.x, Java 21
*   **Data Persistence**:
    *   **Spring Data MongoDB**: Catalog, Reviews, Recently Viewed items.
    *   **Spring Data JPA / Hibernate**: Relational analytics and seller tables in MySQL.
*   **Microservices Architecture**:
    *   **Eureka Discovery Client**: Automates registration and discovery.
    *   **Spring Cloud Config Client**: Centralizes property resolution on startup.
    *   **Zipkin / Micrometer**: Distributed tracing for latency monitoring.
*   **Testing**: JUnit 5, Mockito, Spring Boot Test, Jacoco (Code Coverage)

---

## Configuration & Dependencies

The service runs on port **`8082`** and imports properties from the centralized Config Server. 

### Key Config Repo Properties (`product-service.properties`):
*   **MongoDB URI**: `spring.data.mongodb.uri` (Points to `ecommerceDB` database)
*   **MySQL Database**: `spring.datasource.url` (Points to `jdbc:mysql://localhost:3307/EcommerceDB` database)
*   **Eureka Service Zone**: `eureka.client.service-url.defaultZone` (Points to `http://eureka-server:8761/eureka/`)
*   **Config Server URL**: `spring.config.import` (Imports from `http://config-server:8888`)

---

## REST API Documentation

### 1. Product Catalog Endpoints (`/api/products`)

| Method | Path | Description | Content-Type |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/products` | Create a new product. | `multipart/form-data` |
| **GET** | `/api/products/{id}` | Retrieve a product by its ID. | `application/json` |
| **PUT** | `/api/products/{id}` | Update a product's details and/or images. | `multipart/form-data` |
| **DELETE** | `/api/products/{id}` | Delete a product from the database. | — |
| **GET** | `/api/products/search` | Search products with query parameters (e.g. search, brand, category, minPrice, maxPrice, page, size, sort). | `application/json` |
| **PUT** | `/api/products/{id}/reduce-stock` | Deduct stock during checkout (takes `quantity` param). | `application/json` |
| **PUT** | `/api/products/{id}/increase-stock` | Increment stock on cancellation (takes `quantity` param). | `application/json` |

### 2. Review and Rating Endpoints (`/api/reviews`)

| Method | Path | Description |
| :--- | :--- | :--- |
| **POST** | `/api/reviews` | Create/submit a new product review and rating. |
| **GET** | `/api/reviews/{productId}` | Retrieve all reviews for a specific product. |
| **GET** | `/api/reviews/{productId}/rating` | Get the average rating (1.0 - 5.0) and total review count. |
| **DELETE** | `/api/reviews` | Delete a customer review. |

### 3. Analytics Endpoints

| Service | Method | Path | Description |
| :--- | :--- | :--- | :--- |
| **Seller** | **GET** | `/api/seller/analytics` | Fetch analytics dashboard metrics for the logged-in seller. |
| **Admin** | **GET** | `/api/admin/analytics` | Fetch system-wide catalog statistics and performance metrics for the admin. |

---

## Build, Test, and Run Instructions

### 1. Compile and Package
To build the project and compile the JAR file:
```bash
./mvnw clean package
```
*Note: By default, the tests will be skipped because `<skipTests>true</skipTests>` is enabled in `pom.xml`.*

### 2. Run Tests
To execute the suite of 50+ unit and Mockito-based integration tests:
```bash
./mvnw test -DskipTests=false
```

### 3. Run Locally
To run the Spring Boot application locally:
```bash
./mvnw spring-boot:run
```
*Note: Ensure Config Server (`8888`), Eureka Server (`8761`), MySQL (`3307`), and MongoDB (`27017`) are running.*

### 4. Build Docker Image
To build the lightweight container image locally:
```bash
docker build -t product-service .
```
*(The Dockerfile utilizes an eclipse-temurin:21-jre-alpine container copying the prebuilt target JAR).*
