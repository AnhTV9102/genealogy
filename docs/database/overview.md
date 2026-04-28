# 🧬 Genealogy Database Design

## 🧠 Overview

This database is designed for a **simple genealogy system** using:

* Graph model (Person ↔ Relationship)
* Closure table (`person_tree`) for fast hierarchy queries

### 🎯 Design Goals

* Keep schema simple
* Avoid duplication
* Support genealogy queries:

    * parents
    * children
    * ancestors
    * descendants
* Easy to extend later

---

# 🏗️ Tables

---

## 👤 persons

### Schema

```sql
persons (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(10), -- male, female, unknown
    birth_date DATE,
    death_date DATE
);
```

### Description

Stores basic information about a person.

### Notes

* `gender` is used for filtering (e.g., father/mother)
* Do NOT derive relationships from this table

---

## 🔗 relationships

### Schema

```sql
relationships (
    id BIGINT PRIMARY KEY,

    from_person_id BIGINT NOT NULL,
    to_person_id BIGINT NOT NULL,

    type VARCHAR(20) NOT NULL, 
    -- PARENT_OF, SPOUSE_OF, CHILD_OF, ADOPTED_PARENT_OF

    subtype VARCHAR(20), 
    -- biological, adopted, step

    start_date DATE,
    end_date DATE,

    created_at TIMESTAMP
);
```

---

### Description

Represents relationships between people (graph edges).

---

### Relationship Types

| type   | meaning                  |
| ------ | ------------------------ |
| PARENT_OF | A → B = A is parent of B |
| SPOUSE_OF | A ↔ B = A is spouse of B |
| CHILD_OF | A → B = A is child of B  |
| ADOPTED_PARENT_OF | A → B = A is adopted parent of B |

---

### Rules

#### 1. Direction

* `PARENT_OF`: directional (A → B means A is parent of B)
* `SPOUSE_OF`: bidirectional (A ↔ B means A is spouse of B, must store both directions)

---

#### 2. Do NOT delete

* Divorce → set `end_date` on SPOUSE_OF relationships
* Keep history

---

#### 3. subtype usage

Only for `PARENT_OF` and `ADOPTED_PARENT_OF`:

* biological
* adopted
* step

---

#### 4. Data consistency

* Avoid duplicate relationships of same type between same persons
* Ensure both directions exist for SPOUSE_OF relationships

# 🚨 Common Pitfalls

* Missing `(A, A, 0)` in `person_tree`
* Not updating `person_tree` after insert
* Storing only one direction for SPOUSE_OF relationships
* Deleting relationships instead of ending them with `end_date`
* Using `father/mother` instead of `PARENT_OF`
