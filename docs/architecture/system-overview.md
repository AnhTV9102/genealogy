# 🏗️ Genealogy Backend - System Overview

## 🎯 System Purpose

This backend system is designed to manage **genealogy data (family trees)** with support for:

* Managing individuals (Person)
* Modeling relationships (parent, spouse, etc.)
* Traversing family trees (ancestors / descendants)
* Access control (RBAC)
* Scaling for large, graph-like datasets

---

## 🧠 Architectural Style

The system follows:

> **Clean Architecture (Lite) + MVC (Spring Web)**

### 🔹 Key Principles

* Business logic is independent of frameworks
* Domain layer does not depend on database or infrastructure
* Dependencies always point inward

---

## 🧱 High-Level Architecture

```text
┌──────────────┐
│     API      │  (Spring MVC - Controllers)
└──────┬───────┘
       ↓
┌──────────────┐
│ Application  │  (Use Cases / Services)
└──────┬───────┘
       ↓
┌──────────────┐
│    Domain    │  (Core Business Logic - Pure Java)
└──────┬───────┘
       ↑
┌──────────────┐
│Infrastructure│  (Database, JPA, External Systems)
└──────────────┘
```

---

## 📦 Module-Based Structure (Feature-Oriented)

The system is organized by **feature/domain**, not by technical layers.

```text
com.genealogy
 ├── person
 ├── relationship
 ├── family
 ├── tree
 ├── security
 ├── common
 └── config
```

Each module is self-contained and follows the same architectural pattern.

---

## 🧩 Standard Module Structure

Example: `person`

```text
person/
 ├── api              # REST Controllers (MVC layer)
 ├── application      # Use cases / orchestration
 │    ├── service
 │    ├── dto
 │    └── mapper
 │
 ├── domain           # Core business logic (pure Java)
 │    ├── model
 │    └── repository
 │
 └── infrastructure   # Persistence & external integrations
      ├── entity
      ├── repository
      └── mapper
```

---

## 🧠 Layer Responsibilities

### 1. API Layer (Controller)

* Handles HTTP requests/responses
* Performs basic validation and mapping
* Delegates to application services
* Contains **no business logic**

---

### 2. Application Layer

* Implements use cases (e.g., CreatePerson)
* Orchestrates domain operations
* Converts between DTOs and domain models
* Does not contain complex business rules

---

### 3. Domain Layer (Core)

* Contains **business rules and invariants**
* Framework-independent (no Spring, no JPA)
* Implements **rich domain models**

Example:

```java
person.changeName("John Doe");
person.markAsDeceased(date);
```

✔ Enforces rules (e.g., valid name, valid dates)
✔ Protects internal state

---

### 4. Infrastructure Layer

* Handles technical concerns:

    * Database (JPA)
    * External APIs
* Maps:

    * Entity ↔ Domain
* Implements repository interfaces defined in domain

---

## 🔄 Data Flow

```text
Client Request
     ↓
Controller (API)
     ↓
Application Service
     ↓
Domain Model
     ↓
Repository (interface)
     ↓
Repository Implementation (Infrastructure)
     ↓
Database
```

---

## 🧱 Core Design Principles

### ✅ Dependency Rule

```text
Outer layers → Inner layers
```

* API → Application → Domain
* Infrastructure → Domain

👉 The Domain layer depends on nothing

---

### ✅ Separation of Concerns

| Layer          | Responsibility            |
| -------------- | ------------------------- |
| API            | HTTP / Transport          |
| Application    | Use case orchestration    |
| Domain         | Business logic            |
| Infrastructure | Persistence / integration |

---

### ✅ Rich Domain Model

* Avoid setters
* Use intention-revealing methods

```java
person.changeName(...)
person.markAsDeceased(...)
```

---

## ⚖️ Architecture Level

This project uses:

> **Clean Architecture Lite (Pragmatic approach)**

### Included:

* Domain isolation
* Repository abstraction
* Mapping layers

### Not included (yet):

* Microservices
* CQRS
* Event sourcing

---

## 🚀 Scalability Considerations

This architecture supports:

### 🔹 Feature Scalability

* New modules can be added independently

### 🔹 Domain Scalability

* Complex relationship graphs
* Tree traversal algorithms (DFS/BFS)

### 🔹 System Scalability

* Can evolve to microservices
* Database can be replaced without affecting domain

---

## ⚠️ Trade-offs

| Advantages             | Disadvantages              |
| ---------------------- | -------------------------- |
| High maintainability   | More boilerplate           |
| Strong domain modeling | Steeper learning curve     |
| Easy testing           | Slower initial development |

---

## 🗺️ Architecture Roadmap

### Phase 1 (Current)

* Person module
* Clean Architecture Lite

### Phase 2

* Relationship module (core graph logic)

### Phase 3

* Tree traversal (ancestors / descendants)

### Phase 4

* Performance optimization (caching, indexing)
* Security (RBAC)

---

## 🧩 Summary

The system architecture combines:

* ✔ Feature-based modular structure
* ✔ Clean Architecture (Lite)
* ✔ MVC for API layer

This ensures:

* Long-term maintainability
* Scalability for complex genealogy logic
* Clear separation of concerns

---
