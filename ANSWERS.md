# OOP Oral Exam Answers - Library API

This document contains answers to 50 sets of OOP Oral Exam questions based on the Library Management System project.

---

## Set 1: Class vs Object

**Q1 (60): What is the difference between a class and an object, and why do objects need both fields and methods?**

* **Answer:** A class is a blueprint or template (e.g., `Book.java`), while an object is a specific instance of that
  blueprint created in memory (e.g., a specific copy of "Clean Code"). Objects need fields to store their **state** (
  data like `title`) and methods to define their **behavior** (actions like `borrow()`).

**Q2 (80): In your project, show one class and explain its responsibility, its important fields, and one method that
changes the object’s state.**

* **Answer:** In `Book.java`, the class represents a book entity. Important fields include `availableCopies` and
  `totalCopies`. The method `borrow(int amount)` changes the state by decrementing `availableCopies`.

**Q3 (100): In your project, explain why this part should be modeled using objects instead of writing everything in one
long main() method or using many unrelated variables.**

* **Answer:** Modeling `Book` as an object encapsulates related data and logic. If we used unrelated variables (e.g.,
  `book1Title`, `book1Copies`), it would be impossible to manage a library of thousands of books. Objects allow us to
  create collections (like `List<Book>`) and ensure that logic like "copies cannot be negative" is enforced consistently
  within the object itself.

---

## Set 2: State and Behavior

**Q1 (60): What is the difference between an object’s state and behavior, and how do fields and methods represent them?
**

* **Answer:** State is what an object **knows** (represented by fields), and behavior is what an object **does** (
  represented by methods).

**Q2 (80): Show one object from your project and explain its state and behavior clearly.**

* **Answer:** A `Member` object has the state of `membershipNumber` and `firstName`. Its behavior includes
  `getDisplayInfo()`, which returns a formatted string combining its state.

**Q3 (100): Explain why keeping state and behavior together inside the same class is better than storing data in one
place and controlling it from unrelated code.**

* **Answer:** This is called **Encapsulation**. By keeping state (`availableCopies`) and behavior (`borrow()`) together
  in the `Book` class, we ensure that the state can only be modified in valid ways. If the data were public and
  controlled elsewhere, any part of the program could set `availableCopies` to a negative number, breaking business
  rules.

---

## Set 3: Constructors

**Q1 (60): What is a constructor, and how is it different from a normal method?**

* **Answer:** A constructor is a special block of code called when an object is instantiated. It has no return type and
  must have the same name as the class.

**Q2 (80): Show one constructor in your project and explain what it initializes immediately.**

* **Answer:** In `Book.java`, the constructor `Book(String title, Integer totalCopies)` initializes the title, the total
  capacity, and sets `availableCopies` equal to `totalCopies` to start.

**Q3 (100): Explain what design or runtime problems could happen if objects in your project are created without proper
initialization.**

* **Answer:** If a `Book` is created without initializing `availableCopies`, a `NullPointerException` would occur when
  the `borrow()` method tries to perform subtraction. Proper initialization ensures the object starts in a "valid"
  state.

---

## Set 4: The 'this' Keyword

**Q1 (60): What is the purpose of the this keyword, and when is it especially useful?**

* **Answer:** `this` refers to the current instance of the class. It is especially useful to distinguish between class
  fields and parameters when they have the same name (shadowing).

**Q2 (80): Show one constructor or setter in your project where this helps distinguish a field from a parameter.**

* **Answer:** In the `User` constructor: `this.username = username;`. Here, `this.username` refers to the class field,
  while `username` refers to the constructor parameter.

**Q3 (100): Explain what happens if a programmer forgets to use this when parameter names and field names are the same.
**

* **Answer:** The parameter will be assigned to itself (a no-op), and the class field will remain uninitialized (null or
  default value), leading to logic errors or `NullPointerException` later.

---

## Set 5: Encapsulation

**Q1 (60): What is encapsulation, and how is it related to private and public?**

* **Answer:** Encapsulation is the practice of bundling data and methods into a single unit and restricting access to
  the inner workings. We use `private` to hide fields and `public` to provide a controlled interface (methods).

**Q2 (80): Show one example in your project where a field is hidden and controlled through methods.**

* **Answer:** In `Book.java`, the field `availableCopies` is `private`. It cannot be changed directly from outside; it
  can only be modified through the `borrow()` and `returnCopies()` methods which contain validation logic.

**Q3 (100): If all fields in your project were made public, what correctness or design problems could happen?**

* **Answer:** External code could bypass validation logic (e.g., setting `availableCopies` to -100 or changing a `User`'
  s `role` without permission), leading to an inconsistent database and security vulnerabilities.

---

## Set 6: Getters and Setters

**Q1 (60): Why do programmers use getters and setters, and how is that connected to encapsulation?**

* **Answer:** Getters and setters allow controlled access to private fields. They support encapsulation by allowing us
  to change the internal implementation (e.g., adding logging or validation) without changing the external API.

**Q2 (80): Show one getter or setter in your project and explain what control it gives you.**

* **Answer:** `Librarian.setPosition(LibrarianPosition position)` ensures that the position is set using a specific enum
  type, preventing invalid strings from being assigned to a librarian's role.

**Q3 (100): Explain when a setter should not exist, or when direct modification of data would be dangerous in your
project.**

* **Answer:** A setter for `id` in `BaseEntity` should not be public because the ID is managed by the database. Manually
  changing an ID would break the link between the object and the database record, causing JPA errors.

---

## Set 7: Primitive vs Reference Types

**Q1 (60): What is the difference between a primitive value and a reference type in Java?**

* **Answer:** Primitives (like `int`, `boolean`) store the actual value. Reference types (like `Book`, `User`) store the
  memory address of the object.

**Q2 (80): Explain what happens when two variables refer to the same object.**

* **Answer:** Both variables point to the same memory location. If you modify the object using one variable (e.g.,
  `bookA.setTitle("New")`), the change is visible when accessing the object through the second variable (
  `bookB.getTitle()` will also return "New").

**Q3 (100): Show how shared references could create unexpected side effects in a project if the programmer is careless.
**

* **Answer:** If `BookService` passes a `Book` object to a helper method that unexpectedly modifies its
  `availableCopies`, the original `Book` instance in the Service is changed. This can lead to bugs where data changes in
  one part of the app unintentionally affect another.

---

## Set 8: Passing by Reference

**Q1 (60): What is the difference between passing a primitive value and passing an object reference to a method?**

* **Answer:** Primitives are passed by value (a copy is made). Object references are also passed by value, but the "
  value" is the memory address, so the method works on the same object.

**Q2 (80): Show one method in your project that receives an object as a parameter and explain why.**

* **Answer:** `bookMapper.toDTO(Book book)` receives a `Book` object. It needs the full object to extract multiple
  fields (title, ISBN, etc.) and convert them into a DTO.

**Q3 (100): Explain how object references allow different classes to collaborate on the same data without copying
everything.**

* **Answer:** Instead of copying all the data of a `Book` when moving from `BookService` to `BookMapper`, we just pass
  the reference. This is efficient in terms of memory and ensures they are both looking at the same "source of truth."

---

## Set 9: Inheritance

**Q1 (60): What is inheritance, and how is it different from copying code into multiple classes?**

* **Answer:** Inheritance allows a subclass to acquire properties and behaviors of a superclass. Unlike copying code,
  inheritance creates a relationship where changes in the superclass automatically propagate to subclasses, promoting
  reusability and maintenance.

**Q2 (80): Show one parent-child relationship in your project and explain what the child gets from the parent.**

* **Answer:** `Member extends User`. `Member` inherits fields like `username`, `password`, and `firstName`, as well as
  methods like `getFullName()`.

**Q3 (100): Explain why that relationship is a real is-a relationship and not just code reuse.**

* **Answer:** A `Member` IS-A `User`. Anywhere the system requires a `User` (e.g., for login or profile display), a
  `Member` can be used. It shares the fundamental identity of a user while adding specific details like a
  `membershipNumber`.

---

## Set 10: The 'super' Keyword

**Q1 (60): What is the purpose of the super keyword, and how is it related to inheritance?**

* **Answer:** `super` refers to the immediate parent class. It is used to call parent constructors or invoke overridden
  parent methods.

**Q2 (80): Show where super could be used in your project, either in a constructor or in an overridden method.**

* **Answer:** In `Member.java`, the constructor calls `super(username, password, firstName, lastName, role);` to
  initialize the fields defined in the `User` class.

**Q3 (100): Explain what may go wrong if a child class does not properly initialize the parent part of the object.**

* **Answer:** The inherited fields (like `username`) would remain null or uninitialized. If the system later tries to
  authenticate that `Member`, it will fail because the core `User` data is missing.

---

## Set 11: Method Overriding

**Q1 (60): What is method overriding, and how is it related to inheritance?**

* **Answer:** Overriding is when a subclass provides a specific implementation for a method already defined in its
  superclass. It allows the subclass to change behavior while keeping the same method signature.

**Q2 (80): Show one overridden method in your project and explain why the child version is different from the parent
version.**

* **Answer:** `getDisplayInfo()` is overridden in `Member` and `Librarian`. The `Member` version includes the
  `membershipNumber`, while the `Librarian` version includes their `position`.

**Q3 (100): Explain how overriding helps you avoid writing large if-else blocks based on object type.**

* **Answer:** Instead of writing `if (user instanceOf Member) { ... } else if (user instanceOf Librarian) { ... }`, we
  simply call `user.getDisplayInfo()`. Java's runtime automatically calls the correct version based on the actual object
  type.

---

## Set 12: Polymorphism

**Q1 (60): What is polymorphism in your own words?**

* **Answer:** Polymorphism ("many forms") is the ability of a single interface or reference type to represent different
  underlying forms (objects). It allows one name to represent multiple behaviors.

**Q2 (80): Show one example from your project where one reference can work with different object types.**

* **Answer:** In `UserService`, we might handle a `List<User>`. This list can contain both `Member` and `Librarian`
  objects. We can iterate through the list and call `getDisplayInfo()` on each without knowing their specific type.

**Q3 (100): Explain how polymorphism makes your design easier to extend when new child classes are added.**

* **Answer:** If we add a new user type, say `Admin`, we just need to extend `User` and override `getDisplayInfo()`. The
  existing code that processes `List<User>` doesn't need to change at all.

---

## Set 13: Reference Type vs Object Type

**Q1 (60): What is the difference between a variable’s reference type and the real object type it points to at runtime?
**

* **Answer:** The reference type is determined at compile-time (e.g., `User`), while the object type is the actual class
  instantiated at runtime (e.g., `Member`).

**Q2 (80): Show one example in your project where the reference type is more general than the created object.**

* **Answer:** In `UserFactoryImpl`, the method returns a `User` (reference type), but it actually creates and returns a
  `new Member(...)` or `new Librarian(...)` (object type).

**Q3 (100): Explain how Java decides which overridden method to call at runtime.**

* **Answer:** This is called **Dynamic Method Binding**. Java looks at the actual object type in memory at runtime, not
  the reference type, to decide which version of an overridden method to execute.

---

## Set 14: Interfaces

**Q1 (60): What is an interface, and why is it often described as a contract?**

* **Answer:** An interface is a reference type that only contains method signatures. it's a "contract" because any class
  that implements it promises to provide the implementation for those methods.

**Q2 (80): Show one interface from your project and explain the behavior it promises.**

* **Answer:** `UserFactory` is an interface. It promises a behavior: `createUser`. Any class implementing it (like
  `UserFactoryImpl`) must provide the logic to create a user.

**Q3 (100): Explain why using an interface is better than depending directly on one concrete class in that situation.**

* **Answer:** It decouples the code. `UserService` depends on `UserFactory` (the interface), not `UserFactoryImpl`. If
  we want to change how users are created (e.g., adding a `SocialMediaUserFactory`), we can swap the implementation
  without changing `UserService`.

---

## Set 15: Interfaces and Polymorphism

**Q1 (60): How are interface and polymorphism connected?**

* **Answer:** Interfaces enable polymorphism by allowing different classes that implement the same interface to be
  treated as the same type.

**Q2 (80): Show how one interface in your project could allow multiple implementations.**

* **Answer:** The `CrudService` interface is implemented by `BookService`, `AuthorService`, and `CategoryService`. All
  of them provide `getById`, `create`, `update`, and `delete` logic specific to their entities.

**Q3 (100): Explain how this design helps future extension without changing too much old code.**

* **Answer:** If we add a new feature like `MagazineManagement`, we create `MagazineService` implementing `CrudService`.
  Any generic UI or controller logic that works with `CrudService` will work with the new service automatically.

---

## Set 16: Abstract Classes

**Q1 (60): What is an abstract class, and why can it not be used to create direct objects?**

* **Answer:** An abstract class is a partial blueprint. It can't be instantiated because it might contain abstract
  methods that have no implementation, making the object "incomplete."

**Q2 (80): Show one place in your project where an abstract class would make sense.**

* **Answer:** `User` is an abstract class. It makes sense because there is no such thing as a "generic user" in our
  system—every user must be either a `Member` or a `Librarian`.

**Q3 (100): Explain why making that class abstract is better than making it a normal concrete class.**

* **Answer:** It prevents developers from accidentally creating a `new User()`, which would be useless and lack the
  specific details (like membership number or position) required by the system.

---

## Set 17: Abstract Methods

**Q1 (60): What is an abstract method, and why does it have no body?**

* **Answer:** An abstract method defines **what** a class should do but not **how**. It has no body because the specific
  implementation varies for each subclass.

**Q2 (80): Show one behavior in your project that different child classes could implement differently.**

* **Answer:** `getDisplayInfo()` in the `User` class is abstract. `Member` implements it to show a membership ID, while
  `Librarian` implements it to show a professional title.

**Q3 (100): Explain how abstract methods help a team keep class design consistent.**

* **Answer:** By defining an abstract method in the parent, the architect "forces" every developer who creates a
  subclass to implement that method. This ensures that all user types will always have a way to display their info
  consistently.

---

## Set 18: Abstract Class vs Interface

**Q1 (60): What are two important differences between an abstract class and an interface?**

* **Answer:** 1) A class can only inherit from one abstract class (single inheritance), but can implement many
  interfaces. 2) Abstract classes can have state (fields), while interfaces (traditionally) cannot.

**Q2 (80): Show where your project uses, or should use, an interface or an abstract class.**

* **Answer:** `BaseEntity` is an abstract class because it shares state (`id`, `createdAt`). `UserFactory` is an
  interface because it defines a pure behavior.

**Q3 (100): For that case, justify why one is a better design choice than the other.**

* **Answer:** `BaseEntity` is better as an abstract class because we want all entities to **inherit** the actual
  fields (`id`, `createdAt`) and their JPA mappings. An interface couldn't provide these fields to the subclasses.

---

## Set 19: Has-A vs Is-A Relationship

**Q1 (60): What is a has-a relationship, and how is it different from an is-a relationship?**

* **Answer:** IS-A is inheritance (a `Member` is a `User`). HAS-A is composition (a `Book` has a `Category`).

**Q2 (80): Show one has-a relationship and one is-a relationship from your project, if possible.**

* **Answer:** IS-A: `Librarian extends User`. HAS-A: `Book` has a `Set<Author>`.

**Q3 (100): Explain why choosing the wrong relationship type would make the design weaker or more confusing.**

* **Answer:** If we said `Book extends Author` (IS-A), it would be nonsensical. A book is not a person. You would end up
  with a "Book" having a "birthDate" field, which is logically wrong and makes the code impossible to maintain.

---

## Set 20: Composition vs Inheritance

**Q1 (60): What is composition, and how is it different from inheritance?**

* **Answer:** Inheritance is "IS-A" (extending a class). Composition is "HAS-A" (holding a reference to another class).
  Composition is generally more flexible as it allows for dynamic changes.

**Q2 (80): Show one place in your project where one class contains or uses another class.**

* **Answer:** `Book` contains a `Set<Category>`. This is composition.

**Q3 (100): Explain why composition is a better choice than inheritance for that case.**

* **Answer:** A `Book` is not a `Category`. A book can also belong to **multiple** categories. Inheritance only allows
  one parent, so composition is the only way to model a book that is both "Fiction" and "Mystery."

---

## Set 21: Object Collaboration

**Q1 (60): What does it mean for two objects to collaborate without one inheriting from the other?**

* **Answer:** It means one object uses the services or data of another object through a reference (Dependency Injection
  or method parameters).

**Q2 (80): Show two classes in your project that work together and explain their relationship.**

* **Answer:** `BookService` and `BookRepository`. `BookService` calls methods on `BookRepository` to save or find books.
  They are collaborators, not parent/child.

**Q3 (100): Explain why distributing responsibility across collaborating classes makes the system easier to maintain.**

* **Answer:** This follows the **Separation of Concerns**. If the database logic changes, we only edit `BookRepository`.
  If business rules change, we only edit `BookService`. This prevents a single change from breaking unrelated parts of
  the app.

---

## Set 22: Single Responsibility Principle

**Q1 (60): Why is it useful for a class to have one clear responsibility?**

* **Answer:** It makes the class easier to understand, test, and modify. A class with one responsibility has fewer
  reasons to change.

**Q2 (80): Show one class in your project and explain its main responsibility.**

* **Answer:** `BookMapper`'s sole responsibility is to convert between `Book` entities and `BookDTO`s. It doesn't know
  about databases or business rules.

**Q3 (100): Identify one class that may currently do too much and explain how you would redesign it.**

* **Answer:** `BookService` handles mapping, validation, and persistence. While acceptable for a small project, in a
  larger system, we could move complex validation logic into a separate `BookValidator` class to keep `BookService`
  focused purely on orchestration.

---

## Set 23: Cohesion

**Q1 (60): What is cohesion, and how is it related to good class design?**

* **Answer:** Cohesion refers to how closely related the responsibilities of a class are. High cohesion means a class
  does one thing well, which is a sign of good design.

**Q2 (80): Show one class in your project that has good cohesion.**

* **Answer:** `JwtService`. All its methods (`generateToken`, `validateToken`, `extractUsername`) are strictly related
  to handling JWTs.

**Q3 (100): Explain how weak cohesion can make a class harder to test, understand, or extend.**

* **Answer:** If `JwtService` also handled user registration, you couldn't test JWT logic without also setting up user
  database mocks. It becomes a "God Object" that is brittle and hard to change.

---

## Set 24: Loose Coupling

**Q1 (60): What does loose coupling mean, and why is it valuable in object-oriented design?**

* **Answer:** Loose coupling means classes depend on abstractions (interfaces) rather than concrete implementations. It
  allows you to change one class without affecting others.

**Q2 (80): Identify two parts of your project that should not depend too tightly on each other.**

* **Answer:** `UserController` and `UserFactoryImpl`. The controller should depend on the `UserFactory` interface.

**Q3 (100): Explain how interfaces, better method design, or clearer responsibilities could reduce coupling there.**

* **Answer:** By using the `UserFactory` interface, we can swap `UserFactoryImpl` for a `MockUserFactory` during unit
  testing. The controller doesn't need to know which factory it's using, as long as it gets a `User` back.

---

## Set 25: Static vs Instance Fields

**Q1 (60): What is the difference between an instance field and a static field?**

* **Answer:** An instance field belongs to a specific object (each `Book` has its own `title`). A static field belongs
  to the class itself and is shared by all instances.

**Q2 (80): Show one place in your project where an instance variable makes more sense than a static variable.**

* **Answer:** `availableCopies` in `Book`. Each book must track its own copies; if it were static, all books would share
  the same copy count!

**Q3 (100): Explain a case where using static incorrectly would cause wrong shared behavior between objects.**

* **Answer:** If `username` in the `User` class were static, every time a new user registered, it would overwrite the
  username for ALL existing users in memory.

---

## Set 26: Static vs Instance Methods

**Q1 (60): What is the difference between an instance method and a static method?**

* **Answer:** Instance methods operate on the data of a specific object (`this`). Static methods (utility methods) don't
  need an object and can be called using the class name.

**Q2 (80): Show one method in your project that should clearly be instance-based and explain why.**

* **Answer:** `borrow(int amount)` in `Book`. It must be instance-based because it needs to modify the `availableCopies`
  of that specific book instance.

**Q3 (100): Explain how a project becomes weaker if too many methods are made static.**

* **Answer:** It becomes "procedural" rather than object-oriented. You lose the benefits of polymorphism and inheritance
  because static methods cannot be overridden. It also makes unit testing harder as you can't easily mock static calls.

---

## Set 27: Overloading vs Overriding

**Q1 (60): What is method overloading, and how is it different from method overriding?**

* **Answer:** Overloading is having multiple methods with the same name but different parameters in the same class (
  compile-time). Overriding is replacing a parent's method in a subclass (runtime).

**Q2 (80): Explain one situation in your project where overloading would be useful.**

* **Answer:** In `BookRepository`, we could have overloaded `findByTitle(String title)` and
  `findByTitle(String title, Pageable pageable)` to support both simple and paginated searches.

**Q3 (100): Compare overloading and overriding in terms of extensibility and design value.**

* **Answer:** Overriding is essential for polymorphism and extending system behavior (adding new user types).
  Overloading is just a "convenience" for the developer to provide multiple ways to call the same logic. Overriding has
  much higher architectural value.

---

## Set 28: ArrayList and Collections

**Q1 (60): What is an ArrayList, and how is it different from a normal array?**

* **Answer:** An `ArrayList` is a dynamic array that can grow or shrink in size. A normal array has a fixed size
  determined at creation.

**Q2 (80): Show where you used an ArrayList in your project and explain why it was a better choice.**

* **Answer:** In `BookService`, when converting a `Page<Book>` to a list of DTOs, we use `toList()` which often returns
  an `ArrayList`. It's better because we don't know how many books will be returned from the database ahead of time.

**Q3 (100): Explain how ArrayList makes object management easier when the number of items can change.**

* **Answer:** It handles resizing automatically. If we use a fixed array and the library adds a new book, we would have
  to manually create a bigger array and copy all old data over, which is inefficient and error-prone.

---

## Set 29: Common Type Collections

**Q1 (60): Why is it useful to store objects inside a collection of a common type?**

* **Answer:** It allows you to treat a group of related objects uniformly, for example, processing all "Users" without
  worrying if they are "Members" or "Librarians."

**Q2 (80): Show how inheritance or interfaces allow multiple objects to be kept in the same list.**

* **Answer:** `List<User> users = new ArrayList<>();`. Because `Member` and `Librarian` both inherit from `User`, they
  can both be added to this list.

**Q3 (100): Explain how that design helps when you need to process many similar objects using one loop.**

* **Answer:** You can write a single `for` loop: `for (User u : users) { u.getDisplayInfo(); }`. This loop will work
  correctly for all elements, executing the specialized logic for each type automatically.

---

## Set 30: Validation

**Q1 (60): Why is validation important when changing object state?**

* **Answer:** It ensures that the object always remains in a "valid" state according to business rules, preventing data
  corruption.

**Q2 (80): Show one method in your project that changes an object and explain what should be validated.**

* **Answer:** `Book.borrow(int amount)`. It validates that the `amount` is positive and that there are enough
  `availableCopies`.

**Q3 (100): Explain where object-level rules should be enforced so the rest of the program stays safe.**

* **Answer:** Rules should be enforced **inside the entity** itself (like the `borrow` method in `Book`). This ensures
  that no matter where the object is used (Service, Controller, or even a test), the rules are always applied.

---

## Set 31: Exceptions

**Q1 (60): What is an exception in Java, and how is it different from a compile-time syntax error?**

* **Answer:** An exception is an event that occurs during **runtime** that disrupts the normal flow (e.g., File Not
  Found). A syntax error prevents the code from even compiling.

**Q2 (80): Show one place in your project where invalid data or bad input could cause a runtime exception.**

* **Answer:** In `BookService.getById(Long id)`, if the ID doesn't exist, we throw a `ResourceNotFoundException`.

**Q3 (100): Explain why exception handling should support good object design, not replace proper validation and class
structure.**

* **Answer:** Exceptions should be for "exceptional" cases (unexpected errors). They shouldn't be used for basic flow
  control (like validating a username). Proper class design (using types and validation) prevents errors before they
  need to be caught as exceptions.

---

## Set 32: Try-Catch

**Q1 (60): What is the purpose of a try-catch block in practical program design?**

* **Answer:** It allows the program to handle errors gracefully instead of crashing. You "try" a risky operation and "
  catch" the error to log it or show a user-friendly message.

**Q2 (80): Show one place in your project where a try-catch block could prevent the program from crashing.**

* **Answer:** In `GlobalExceptionHandler`, we catch various exceptions and return an `ErrorResponseDTO`. This prevents
  the entire Spring Boot server from crashing or returning a generic white-label error page to the user.

**Q3 (100): Explain why it is bad practice to rely on try-catch everywhere instead of designing objects and validation
properly.**

* **Answer:** Overusing try-catch makes code hard to read and can hide bugs. It's much better to check
  `if (book != null)` than to catch a `NullPointerException`. Validation is proactive; try-catch is reactive.

---

## Set 33: Preventing vs Catching

**Q1 (60): What is the difference between preventing an error and catching an error after it happens?**

* **Answer:** Preventing means checking conditions beforehand (e.g., `if (copies > 0)`). Catching means letting the
  error happen and then dealing with the fallout.

**Q2 (80): Show one rule in your project that should be checked before the risky operation happens.**

* **Answer:** In `Book.borrow()`, we check `if (this.availableCopies < amount)` before we actually subtract the copies.

**Q3 (100): Compare validation through object methods with handling an exception afterward. Which should come first, and
why?**

* **Answer:** Validation should come first. It is cleaner, more performant (exceptions are expensive to create), and
  makes the "happy path" of the code much clearer to read.

---

## Set 34: Variable Scope

**Q1 (60): What is variable scope, and what is the difference between a field and a local variable?**

* **Answer:** Scope is the region of code where a variable is accessible. A field belongs to the class and lasts as long
  as the object. A local variable is defined inside a method and "dies" when the method finishes.

**Q2 (80): Show one method in your project and identify its local variables and class fields.**

* **Answer:** In `Book.borrow(int amount)`, `amount` is a local variable (parameter). `this.availableCopies` is a class
  field.

**Q3 (100): Explain how poor scope decisions can create confusion or bugs in object-oriented code.**

* **Answer:** If you use a class field for a temporary calculation that should have been a local variable, you might
  accidentally leave data from a previous method call in that field, causing the next call to produce wrong results.

---

## Set 35: Naming Conventions

**Q1 (60): Why is naming important in class and method design?**

* **Answer:** Good naming makes code "self-documenting." A programmer should be able to understand what a class or
  method does just by reading its name.

**Q2 (80): Show one class name and one method name from your project and explain why they are appropriate or not.**

* **Answer:** `BookService` is appropriate because it clearly provides services related to Books. `borrow()` is
  appropriate because it matches the real-world library action.

**Q3 (100): Explain how poor naming can confuse object responsibility, inheritance meaning, or class interaction.**

* **Answer:** If `Member` was named `DataContainer`, we wouldn't know it's a type of `User`. If `borrow()` was named
  `update()`, we wouldn't know it specifically handles the business logic of checking availability and decrementing
  counts.

---

## Set 36: Access Modifiers

**Q1 (60): What is the role of access modifiers like private, protected, and public in OOP?**

* **Answer:** They control visibility. `private` = same class only. `protected` = same package + subclasses. `public` =
  everyone.

**Q2 (80): Show one class in your project and explain why some members should not all have the same access level.**

* **Answer:** In `User`, `username` is `private` to protect the data. `getFullName()` is `public` so it can be used in
  the UI. `validate()` might be `protected` so subclasses can use it but external controllers cannot.

**Q3 (100): Explain how poor access control can make inheritance, maintenance, or debugging more difficult.**

* **Answer:** If everything is `public`, any class can change the internal state of another. When a bug occurs, you have
  to check the entire project to see who changed the data, instead of just checking the class itself.

---

## Set 37: Hiding Implementation

**Q1 (60): What is the benefit of hiding implementation details behind methods?**

* **Answer:** It allows you to change how a method works (e.g., switching from a local list to a database) without
  breaking all the other classes that use that method.

**Q2 (80): Show one example in your project where other classes use a method without needing to know the internal logic.
**

* **Answer:** `AuthController` calls `authService.login()`. The controller doesn't know about JWT generation, BCrypt
  password checking, or database queries; it just knows it gets a token back.

**Q3 (100): Explain how this kind of encapsulation makes debugging and modification easier later.**

* **Answer:** If the token format changes, you only modify `JwtService`. You don't have to touch the `AuthController` or
  any other part of the app, as the "interface" (the method signature) stayed the same.

---

## Set 38: Reusability

**Q1 (60): Why is reusability an important goal in OOP?**

* **Answer:** It saves time, reduces code duplication, and makes the system more reliable (since you're using tested
  code).

**Q2 (80): Show one class or method in your project that can be reused in multiple situations.**

* **Answer:** `BaseEntity` is reused by `Book`, `User`, `Author`, and `Category` to provide common ID and timestamp
  fields.

**Q3 (100): Explain what design choices made that reuse possible.**

* **Answer:** Using an **Abstract Class** (`BaseEntity`) and a **MappedSuperclass** annotation allowed us to define the
  common structure once and share it across completely different entities.

---

## Set 39: Inheritance Limitations

**Q1 (60): Why is inheritance not always the best solution in OOP?**

* **Answer:** Inheritance creates a "tight coupling" between parent and child. If the parent changes, it might break the
  child. It also doesn't support multiple inheritance in Java.

**Q2 (80): Give one example of a situation where inheritance seems possible but may not be the best design.**

* **Answer:** If we made `Loan` inherit from `Book` (because a loan involves a book). This would be wrong because a loan
  IS NOT a book.

**Q3 (100): Compare inheritance and composition for that case, and justify which one would be more maintainable.**

* **Answer:** Composition is better: `Loan` has a reference to `Book`. This is more maintainable because a `Loan` can
  then also have a reference to a `Member` and a `Librarian` without creating a messy inheritance tree.

---

## Set 40: Polymorphism vs If-Else

**Q1 (60): Why is using many if-else checks on object type often a sign of weak OOP design?**

* **Answer:** It makes the code hard to extend. Every time you add a new type, you have to find and update every
  `if-else` block in the entire project.

**Q2 (80): Show one place in a project where polymorphism could replace repeated type checking.**

* **Answer:** Instead of checking `if (user instanceof Member)` to decide how to print a report, we just call
  `user.getDisplayInfo()`.

**Q3 (100): Explain how redesigning that part with overriding or interfaces would make the code cleaner.**

* **Answer:** The code becomes a simple one-liner. The "decision" of how to act is moved inside the object where it
  belongs, making the calling code cleaner and more robust.

---

## Set 41: Generalization and Specialization

**Q1 (60): What is the difference between a general class and a specialized subclass?**

* **Answer:** A general class (`User`) contains shared attributes. A specialized subclass (`Librarian`) adds attributes
  and behaviors unique to that specific role.

**Q2 (80): Show one general idea and one specialized idea from your project that fit a parent-child design.**

* **Answer:** General: `User` (identity, login). Specialized: `Member` (borrowing history, membership number).

**Q3 (100): Explain how to decide whether specialization should be modeled with inheritance or handled another way.**

* **Answer:** Use the **IS-A test**. If a `Librarian` is truly a `User`, use inheritance. if they just "have" a user
  account, use composition.

---

## Set 42: Object Responsibility

**Q1 (60): What does it mean for an object to be responsible for its own behavior?**

* **Answer:** It means the object should contain the logic that manipulates its own data, rather than having external
  classes reach in and change its fields.

**Q2 (80): Show one method in your project that belongs inside the class because it uses or protects that object’s own
data.**

* **Answer:** `Book.updateTotalCopies(int newTotal)`. This logic belongs in `Book` because it needs to check
  `availableCopies` (its own data) to ensure the new total isn't less than the number of books currently borrowed.

**Q3 (100): Explain why moving that behavior outside the class would weaken encapsulation or create duplication.**

* **Answer:** If moved to `BookService`, the `Book` fields would have to be public. If another service (like
  `InventoryService`) also needs to update totals, we would have to duplicate the validation logic there, leading to
  bugs.

---

## Set 43: Constructors and Validity

**Q1 (60): What is the relationship between constructors and object validity?**

* **Answer:** The constructor's job is to ensure that the object is in a valid, usable state the moment it is created.

**Q2 (80): Explain one important rule that should always be true for an object in your project.**

* **Answer:** For a `Book`, `availableCopies` must always be less than or equal to `totalCopies`.

**Q3 (100): Show how constructors and state-changing methods can help guarantee that rule remains true.**

* **Answer:** The constructor sets them equal. The `borrow()` method decreases `availableCopies`, and
  `updateTotalCopies()` checks the current borrowed count before allowing a change to `totalCopies`. This ensures the
  rule is never broken.

---

## Set 44: Logic Ownership

**Q1 (60): Why is it useful to think about “who should do this work” when designing OOP classes?**

* **Answer:** It helps in distributing responsibilities correctly (GRASP patterns). Putting logic in the wrong place
  leads to "spaghetti code" and tight coupling.

**Q2 (80): Show one feature in your project and explain which class should own that logic.**

* **Answer:** Generating a membership number. This logic should be owned by the `Member` class (or a dedicated service
  it uses), because it's a fundamental part of what it means to be a `Member`.

**Q3 (100): Explain a case where putting the logic in the wrong class would hurt readability, cohesion, or reuse.**

* **Answer:** If the `UserController` generated membership numbers, we couldn't create a `Member` in a test or a
  background job without duplicating that generation logic.

---

## Set 45: State and Behavior Connection

**Q1 (60): Why should object state and behavior stay logically connected?**

* **Answer:** To maintain high cohesion and follow the Principle of Least Astonishment. You expect a `Book` to know how
  to be borrowed, not an `Author`.

**Q2 (80): Show one class in your project where fields and methods clearly belong together.**

* **Answer:** `User`. The fields (username, password) and behavior (password validation) are intrinsically linked.

**Q3 (100): Explain what design problem appears if methods change data that belongs conceptually to another class.**

* **Answer:** This is called "Feature Envy." It leads to tight coupling because Class A must know everything about the
  internal structure of Class B, making the system fragile.

---

## Set 46: Current Needs vs Extension

**Q1 (60): What is the difference between designing for current needs and designing for extension?**

* **Answer:** Designing for current needs is "YAGNI" (You Ain't Gonna Need It). Designing for extension means using
  patterns (like Polymorphism) so new features can be added without rewriting old code.

**Q2 (80): Show one part of your project that may need new object types or new behaviors later.**

* **Answer:** The `Loan` system. Currently it's a simple loan, but later we might need `LateLoan`, `DamagedLoan`, or
  `ReferenceOnlyLoan`.

**Q3 (100): Explain how your current design supports or fails to support that future extension.**

* **Answer:** By using a `Loan` class that can be extended, and a `LoanService` that works with the base `Loan` type, we
  can add new loan types with specialized rules without breaking the existing borrowing flow.

---

## Set 47: Extending vs Modifying

**Q1 (60): What is the difference between extending a system and modifying existing code everywhere?**

* **Answer:** Extending means adding new classes (Open-Closed Principle). Modifying means changing existing, tested
  code, which is risky and can introduce regressions.

**Q2 (80): Show one area in your project where adding a new object type should require minimal changes.**

* **Answer:** Adding a new `Category` or `Author`. Because they are just data in the database and handled generically by
  `BookService`, no code changes are needed to support a "Sci-Fi" category.

**Q3 (100): Explain which OOP principles help reduce the need to edit many old classes when a new feature is added.**

* **Answer:** **Polymorphism** and the **Open-Closed Principle**. By depending on abstractions, the core logic stays the
  same while the "details" (new subclasses) provide the new functionality.

---

## Set 48: Testability

**Q1 (60): Why is testing easier when classes have clear inputs, outputs, and responsibilities?**

* **Answer:** You can write "Unit Tests" that isolate a single piece of logic. If a class does too much, you have to set
  up a massive environment just to test one small thing.

**Q2 (80): Show one class or method in your project that would be easy to test and explain why.**

* **Answer:** `User.getFullName()`. It has no dependencies and always returns a predictable output based on the input
  fields.

**Q3 (100): Explain how bad coupling or unclear responsibilities make OOP code harder to test and debug.**

* **Answer:** If `BookService` is tightly coupled to a real MySQL database, you can't test it without a database
  running. This makes tests slow and unreliable. Loose coupling (using repositories) allows you to "mock" the database
  for fast testing.

---

## Set 49: Refactoring Signs

**Q1 (60): What are common signs that an OOP design needs refactoring?**

* **Answer:** "Code Smells" like long methods, large classes (God Objects), duplicate code, and high coupling (one
  change breaks ten things).

**Q2 (80): Point to one part of your project that could be improved in structure, naming, access control, or class
responsibility.**

* **Answer:** The `DataInitializer` class is becoming large. It handles creating users, authors, and books.

**Q3 (100): Propose a refactoring for that part and explain why the new design would be better.**

* **Answer:** Split `DataInitializer` into `UserDataInitializer`, `BookDataInitializer`, etc. This would follow the
  Single Responsibility Principle and make the setup process easier to manage as the test data grows.

---

## Set 50: The Three Pillars

**Q1 (60): Define the three main pillars of OOP used in this course: encapsulation, inheritance, and polymorphism.**

* **Answer:** 1) **Encapsulation**: Hiding data and protecting it with methods. 2) **Inheritance**: Creating
  hierarchical relationships for reuse. 3) **Polymorphism**: Allowing different types to be treated as a common type.

**Q2 (80): Point to one concrete example of each of these three pillars in your project.**

* **Answer:** 1) **Encapsulation**: Private fields in `Book.java`. 2) **Inheritance**: `Member extends User`. 3) *
  *Polymorphism**: `List<User>` containing both `Member` and `Librarian`.

**Q3 (100): Evaluate your project design: what is one strong OOP decision you made, and what is one weak part you would
improve next?**

* **Answer:** **Strong**: Using a `UserFactory` and inheritance for different user roles; it makes the system very
  extensible. **Weak**: The `Book` entity is starting to have a lot of business logic (borrow, return, update). I might
  move some of this into a `LoanDomainService` to keep the `Book` entity cleaner as the rules for borrowing get more
  complex (fines, reservations, etc.).
