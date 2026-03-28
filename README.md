# Library Management System

A RESTful Library Management System built with **Spring Boot 3.x**, focusing on **Domain-Driven Design (DDD)** and robust **Object-Oriented Programming (OOP)** principles.

## 🚀 Features

- **Book Management:** CRUD operations for books, authors, and categories.
- **Member Management:** Secure registration and role-based access for Members and Librarians.
- **Loan System:** Automated borrowing and returning process with built-in stock management.
- **Rich Domain Logic:** Entities encapsulate their own state and business rules (Encapsulation).
- **Flexible Policies:** Strategy-based borrowing limits using Functional Interfaces and Lambdas.
- **Secure Auth:** JWT-based authentication with Access and Refresh Token rotation.
- **Search & Filter:** Advanced searching using JPA Specifications.

## 🏗️ Architecture & OOP Principles

This project serves as a showcase for high-quality software engineering practices:

- **Rich Domain Model:** Business invariants are enforced within entities (e.g., `Book.borrow()`, `Loan.returnBook()`), following the **"Tell, Don't Ask"** principle.
- **Factory Pattern:** Centralized user creation via `UserFactory` to decouple services from concrete implementations.
- **Strategy Pattern:** Borrowing rules are abstracted into a `BorrowingPolicy` interface, allowing for easy extension without modifying core service code.
- **SOLID Principles:** Strict adherence to SRP, OCP, and DIP to ensure a maintainable and scalable codebase.
- **Clean Code:** Focused on readability, meaningful naming, and minimal coupling.

For a detailed technical breakdown, see [OOP_SUMMARY.md](./OOP_SUMMARY.md).

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.4.2
- **Language:** Java 21
- **Database:** MySQL 8.0
- **Security:** Spring Security & JWT
- **Persistence:** Spring Data JPA / Hibernate
- **Mapping:** Manual DTO-Entity Mappers
- **Testing:** JUnit 5, Mockito, AssertJ

## 🚦 Getting Started

### Prerequisites
- JDK 21+
- Maven 3.9+
- Docker (for MySQL)

### 1. Database Setup
Start the MySQL container using Docker Compose:
```bash
docker compose up -d
```
*Note: MySQL is configured to run on port `3308`.*

### 2. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```
The API will be available at `http://localhost:8080/api/v1/`.

### 3. API Documentation
Once the app is running, access the interactive Swagger UI:
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Docs:** `http://localhost:8080/v3/api-docs`

## 🧪 Testing
The project includes a comprehensive suite of unit tests for both the domain and service layers.
```bash
mvn test
```

## 🔐 Security
Most endpoints require a valid JWT in the `Authorization` header:
`Authorization: Bearer <your_token>`

Default users (created by `DataInitializer`):
- **Librarian:** `sovath` / `password123`
- **Member:** `onion` / `password123`

## 📂 Project Structure
```text
src/main/java/com/oop/library_management/
├── auth/           # Authentication & Token logic
├── author/         # Author management
├── book/           # Book management & Stock logic
├── category/       # Book categorization
├── common/         # Base entities and shared utilities
├── exception/      # Global exception handling
├── loan/           # Borrowing/Returning logic & Policies
├── security/       # JWT and Security configuration
├── user/           # User hierarchy & Factory logic
└── validation/     # Custom validators
```

## 📄 License
This project is for educational purposes and follows standard OOP best practices.
