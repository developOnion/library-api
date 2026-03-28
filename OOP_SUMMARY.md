# Object-Oriented Programming (OOP) Architecture Summary

This document outlines the core OOP principles and design patterns implemented within the Library Management System. The
architecture follows a **Rich Domain Model** approach, prioritizing encapsulation, maintainability, and clear separation
of concerns.

## 1. Encapsulation & Rich Domain Model

The system has transitioned from an "Anemic Domain Model" to a **Rich Domain Model**, where business logic resides
within the entities themselves.

- **State Protection:** Internal fields such as `availableCopies` in the `Book` entity and `status` in the `Loan` entity
  utilize `private` setters. This prevents external services from forcing the object into an invalid state.
- **Invariant Enforcement:** Entities are responsible for validating their own business rules and data formats.
    - The `Book` entity ensures total copies never drop below the number of borrowed books.
    - The `User` entity enforces username and password format requirements via JPA lifecycle hooks (`@PrePersist`,
      `@PreUpdate`), ensuring domain integrity is maintained regardless of the creation source.
- **Atomic Transitions:** Methods like `Loan.returnBook()` synchronize multiple state changes (e.g., updating both the
  status and the return date) to ensure internal consistency.

## 2. Factory Pattern for Object Creation

To further decouple the service layer from concrete implementations, the system employs the **Factory Pattern** for the
`User` hierarchy.

- **Centralized Instantiation:** The `UserFactory` and its implementation `UserFactoryImpl` encapsulate the logic
  required to instantiate different types of users (`Member`, `Librarian`).
- **Decoupling:** `UserService` no longer needs to know about the specific constructors or unique requirements (like
  `LibrarianPosition`) of concrete user types during registration. It simply requests a user from the factory based on
  the requested role.

## 3. Abstraction & Strategy Pattern

The system leverages functional programming features to abstract complex business rules away from service orchestration.

- **Functional Interfaces:** The `BorrowingPolicy` interface abstracts the logic for validating borrowing requests. This
  decouples the `LoanService` from specific hardcoded limits.
- **Strategy Pattern:** By using a policy-based approach, the system adheres to the **Open/Closed Principle**. New
  borrowing rules (e.g., for VIP members) can be introduced via new implementations of the `BorrowingPolicy` without
  modifying the core service logic.

## 4. Polymorphism via Lambda Expressions

Modern Java features are employed to implement behavioral polymorphism cleanly and efficiently.

- **Lambda Implementation:** In `LoanService`, the default borrowing policy is implemented as a lambda expression:
  `(requested, current) -> (requested + current) <= 5`.
- **Dynamic Behavior:** This allows the service to treat business logic as a parameter, making the system highly
  flexible and easy to extend.

## 5. Inheritance & Method Overriding

A structured inheritance hierarchy is used to promote code reuse and provide specialized behavior where necessary.

- **`BaseEntity` Base Class:** Centralizes common persistence fields (`id`, `createdAt`, `updatedAt`), ensuring a
  consistent structure across all database entities.
- **`User` Hierarchy:** An abstract `User` class defines core identity and authentication logic, while `Member` and
  `Librarian` subclasses provide specialized role-based behavior.
- **Polymorphic Dispatch:** The abstract `getDisplayInfo()` method in the `User` class is overridden by subclasses,
  allowing the system to interact with any user type polymorphically while retrieving role-specific information.

## 6. "Tell, Don't Ask" Principle

The architecture follows the "Tell, Don't Ask" principle to ensure that logic remains co-located with the data it
operates on.

- **Surgical Logic Placement:** Instead of services querying entity state to perform calculations, they "tell" the
  entity to perform the action (e.g., `book.borrow(amount)` or `user.validate()`).
- **Reduced Coupling:** This approach minimizes the surface area of entities exposed to the service layer, making the
  system more robust to changes in business rules.

## 7. SOLID Principle Alignment

- **Single Responsibility (SRP):** Entities handle state and business invariants; factories handle object creation;
  services handle orchestration and transactions.
- **Open/Closed (OCP):** New user roles can be added to the `UserFactory` and new borrowing rules to the
  `BorrowingPolicy` without modifying existing service code.
- **Dependency Inversion (DIP):** High-level services depend on abstractions (`UserFactory`, `BorrowingPolicy`) rather
  than concrete implementations or hardcoded logic.

