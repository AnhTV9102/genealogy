# 📦 Backend Structure Guide

## 🎯 Purpose

This document explains the **project folder structure** and the responsibility of each package in the backend system.

The goal is to ensure:

* Consistency across modules
* Clear separation of concerns
* Maintainability as the system grows

---

## 🧠 High-Level Structure

```text
src/main/java/com.genealogy
 ├── person
 ├── relationship
 ├── family
 ├── tree
 ├── security
 ├── common
 ├── config
 └── GenealogyApplication
```

---

## 📦 Root-Level Packages

### 🔹 `person/`, `relationship/`, `family/`, `tree/`

These are **feature modules (domain-driven)**.

Each module:

* Is self-contained
* Follows the same internal structure
* Represents a business domain

---

### 🔹 `security/`

Contains all **authentication and authorization logic**.

Typical contents:

```text
security/
 ├── jwt
 ├── filter
 ├── config
 └── service
```

Responsibilities:

* JWT handling
* Authentication filters
* Security configuration
* RBAC (role-based access control)

---

### 🔹 `common/`

Shared utilities and cross-cutting concerns.

```text
common/
 ├── exception
 ├── response
 └── util
```

Responsibilities:

* Global exception handling
* Standard API response format
* Utility classes

---

### 🔹 `config/`

Application-wide configuration.

```text
config/
 ├── database
 ├── web
 └── app
```

Examples:

* CORS configuration
* Jackson configuration
* Bean definitions

---

### 🔹 `GenealogyApplication`

Main entry point of the application.

---

## 🧩 Internal Structure of a Module

Each feature module (e.g., `person`) follows this structure:

```text
person/
 ├── api
 ├── application
 ├── domain
 └── infrastructure
```

---

## 🔹 1. `api/` — Presentation Layer (MVC)

Handles HTTP communication.

```text
api/
 └── PersonController
```

Responsibilities:

* Define REST endpoints
* Handle request/response
* Validate input (basic)
* Call application services

❗ Rules:

* No business logic
* No direct DB access

---

## 🔹 2. `application/` — Use Case Layer

Contains orchestration logic.

```text
application/
 ├── service
 ├── dto
 └── mapper
```

### 📁 `service/`

* Implements use cases
* Coordinates domain + repository

Example:

* `createPerson`
* `updatePerson`

---

### 📁 `dto/`

Data Transfer Objects

* Request objects (input)
* Response objects (output)

Example:

```java
CreatePersonRequest
PersonResponse
```

---

### 📁 `mapper/`

Converts:

* DTO ↔ Domain

---

## 🔹 3. `domain/` — Core Business Layer

The most important layer.

```text
domain/
 ├── model
 └── repository
```

---

### 📁 `model/`

Contains **domain models (rich models)**

Example:

```java
Person
Relationship
```

Rules:

* No framework annotations
* No JPA
* Encapsulated logic
* No public setters

---

### 📁 `repository/`

Defines **interfaces (contracts)**

Example:

```java
PersonRepository
```

Rules:

* No implementation
* No Spring dependency

---

## 🔹 4. `infrastructure/` — Technical Layer

Handles persistence and external systems.

```text
infrastructure/
 ├── entity
 ├── repository
 └── mapper
```

---

### 📁 `entity/`

JPA entities (database representation)

Example:

```java
@Entity
class PersonEntity
```

---

### 📁 `repository/`

Implements domain repositories.

Example:

```java
class PersonRepositoryImpl implements PersonRepository
```

Also includes:

```java
JpaPersonRepository extends JpaRepository
```

---

### 📁 `mapper/`

Converts:

* Entity ↔ Domain

---

## 🔄 Layer Interaction Rules

```text
API → Application → Domain
Infrastructure → Domain
```

### ❗ Important Rules

* Domain must NOT depend on:

    * Spring
    * JPA
    * Infrastructure

* Application must NOT depend on:

    * Entity (JPA)

* API must NOT:

    * access repository directly

---

## 🧠 Example Flow

```text
POST /api/v1/persons
        ↓
PersonController
        ↓
PersonService (application)
        ↓
Person (domain)
        ↓
PersonRepository (interface)
        ↓
PersonRepositoryImpl (infrastructure)
        ↓
Database
```

---

## ⚖️ Design Decisions

### ✔ Feature-Based Structure

Instead of:

```text
controller/
service/
repository/
```

We use:

```text
person/
relationship/
```

Benefits:

* Better modularity
* Easier scaling
* Clear ownership

---

### ✔ Clean Architecture (Lite)

We enforce:

* Domain isolation
* Dependency direction
* Separation of concerns

But avoid:

* Over-engineering
* Excessive abstraction

---

## ⚠️ Common Mistakes to Avoid

### ❌ Putting business logic in Controller

### ❌ Using Entity in API layer

### ❌ Adding `@Entity` in Domain

### ❌ Creating global service layer

### ❌ Overusing DTO mapping unnecessarily

---

## 🚀 Scaling Strategy

As the system grows:

### 🔹 Add new modules

* `notification`
* `audit`
* `media`

### 🔹 Split modules into microservices (future)

### 🔹 Optimize infrastructure

* caching
* indexing
* graph database

---

## 🧩 Summary

This structure ensures:

* Clear boundaries between layers
* Domain-driven design
* Scalability for complex genealogy logic

> ✔ Feature-based
> ✔ Clean Architecture (Lite)
> ✔ Production-ready

---
