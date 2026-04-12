# Library Management API

A robust RESTful API for a Library Management System built with Spring Boot 4.0.2 and Java 21. This system manages books, authors, categories, and handles a multi-role borrowing/return process.

## Core Features

- **User Authentication & Authorization**: Secure access using JWT (JSON Web Tokens) with distinct roles for **Librarians** and **Members**.
- **User Management**: Factory-based user creation for Librarians and Members.
- **Book Inventory**: Complete CRUD operations for books, including ISBN tracking, quantity management, and categorization.
- **Author & Category Management**: Organize the library with detailed author profiles and categorized book collections.
- **Advanced Loan System**: 
    - Borrow multiple books in a single transaction.
    - Track return deadlines and borrowing periods.
    - Support for partial or full returns of borrowed quantities.
- **Search & Pagination**: Efficient searching and filtering for books, authors, and categories using JPA Specifications.
- **Database Versioning**: Flyway-managed schema migrations and initial data seeding.
- **Documentation**: Interactive API documentation with Swagger UI.

## Core Endpoints & Example Payloads

### 1. Authentication
**POST** `/api/v1/auth/login`
```json
{
  "username": "onion",
  "password": "@Onionadmindev66$"
}
```

### 2. User Registration
#### Member
**POST** `/api/v1/users/register/members` (Requires LIBRARIAN authority)
```json
{
  "username": "sovath123",
  "password": "@Sovath123",
  "firstName": "Ngov",
  "lastName": "Lysovath"
}
```

#### Librarian
**POST** `/api/v1/users/register/librarians` (Requires LIBRARIAN authority)
```json
{
  "username": "new_librarian",
  "password": "@SecurePassword123",
  "firstName": "Jane",
  "lastName": "Doe",
  "position": "HEAD_LIBRARIAN"
}
```

### 3. Book Management
**POST** `/api/v1/books` (Requires LIBRARIAN authority)
```json
{
  "title": "Foundation",
  "isbn": "978-0553293357",
  "totalCopies": 5,
  "authorIds": [100],
  "categoryIds": [105]
}
```

### 4. Loan System
#### Borrow Books
**POST** `/api/v1/loans/borrow` (Requires LIBRARIAN authority)
```json
{
  "membershipNumber": "MEM-00003",
  "bookAmounts": [
    {
      "bookId": 500,
      "amount": 3
    },
    {
      "bookId": 274,
      "amount": 2
    }
  ],
  "periodDays": 7
}
```

#### Return Books
**PUT** `/api/v1/loans/return` (Requires LIBRARIAN authority)
```json
{
  "membershipNumber": "MEM-00003",
  "bookAmounts": [
    {
      "bookId": 500,
      "amount": 1
    }
  ]
}
```

#### Loan History
**GET** `/api/v1/loans/history/{userId}?status=BORROWED&page=0&size=10`
- Accessible by LIBRARIAN for any user.
- Accessible by MEMBER for their own ID.

## Technology Stack

- **Framework**: [Spring Boot 4.0.2](https://spring.io/projects/spring-boot)
- **Language**: [Java 21](https://www.oracle.com/java/technologies/downloads/)
- **Security**: [Spring Security](https://spring.io/projects/spring-security) & [JJWT (Java JWT)](https://github.com/jwtk/jjwt)
- **Database**: [MySQL 8.0](https://www.mysql.com/) & [Flyway](https://flywaydb.org/) for migrations
- **ORM**: [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (Hibernate)
- **Documentation**: [SpringDoc OpenAPI](https://springdoc.org/) (Swagger UI)
- **Build Tool**: [Maven](https://maven.apache.org/)
- **Containerization**: [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)
