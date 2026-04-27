# 🔗 Relationship Architecture (Graph-Based Design)

## 🎯 Purpose

The **Relationship module** is the core of the genealogy system.

While `Person` stores individual data, **Relationship defines how people are connected**, enabling:

* Parent / Child relations
* Spouse relations
* Extended family (grandparents, siblings, etc.)
* Tree traversal (ancestors / descendants)

---

## ⚠️ Why NOT use simple foreign keys?

A naive approach:

```text
Person
 ├── fatherId
 ├── motherId
 └── spouseId
```

### ❌ Problems:

* Cannot support:

    * Multiple spouses
    * Adoption
    * Complex lineage
* Hard to query:

    * grandparents
    * cousins
* Breaks when family structure changes (divorce, remarriage)
* Not scalable for large trees

---

## ✅ Solution: Graph-Based Model

We model genealogy as a **directed graph**

* **Node** → Person
* **Edge** → Relationship

```text
(Person A) ──PARENT_OF──▶ (Person B)
(Person A) ──SPOUSE_OF──▶ (Person C)
```

---

## 🧱 Core Model

### Relationship Entity (Conceptual)

```text
Relationship
 ├── id
 ├── fromPersonId
 ├── toPersonId
 ├── type
 ├── metadata (optional)
```

---

## 🧠 Relationship Types

```text
PARENT_OF
CHILD_OF
SPOUSE_OF
ADOPTED_PARENT_OF
```

👉 Prefer **directional relationships**:

* `PARENT_OF` instead of storing both parent & child
* Query can derive inverse (`CHILD_OF`)

---

## 🔁 Bidirectional Handling

Instead of storing both:

```text
A → PARENT_OF → B
B → CHILD_OF → A
```

👉 Store **one direction only**

Then derive:

```text
child = find where from = parent AND type = PARENT_OF
```

✔ Avoid duplication
✔ Avoid inconsistency

---

## 🧩 Database Design

### Table: `relationships`

```sql
id BIGINT PK
from_person_id BIGINT
to_person_id BIGINT
type VARCHAR
created_at TIMESTAMP
```

---

### Indexing (IMPORTANT)

```sql
CREATE INDEX idx_from_person ON relationships(from_person_id);
CREATE INDEX idx_to_person ON relationships(to_person_id);
CREATE INDEX idx_type ON relationships(type);
```

👉 Required for fast traversal

---

## 🧠 Domain Model (Clean Architecture)

### Relationship (Domain)

```java
public class Relationship {

    private Long id;
    private Long fromPersonId;
    private Long toPersonId;
    private RelationshipType type;

    private Relationship(Long id, Long from, Long to, RelationshipType type) {
        validate(from, to, type);
        this.id = id;
        this.fromPersonId = from;
        this.toPersonId = to;
        this.type = type;
    }

    public static Relationship create(Long from, Long to, RelationshipType type) {
        return new Relationship(null, from, to, type);
    }

    public static Relationship restore(Long id, Long from, Long to, RelationshipType type) {
        return new Relationship(id, from, to, type);
    }

    private void validate(Long from, Long to, RelationshipType type) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Self relationship is not allowed");
        }
    }

    public boolean isParentOf() {
        return type == RelationshipType.PARENT_OF;
    }
}
```

---

## 🔐 Invariants (VERY IMPORTANT)

The system must enforce:

### ❗ No self relationship

```text
A → A ❌
```

### ❗ No circular parent

```text
A → B → A ❌
```

### ❗ Spouse uniqueness rule (optional)

```text
A → SPOUSE_OF → B
A → SPOUSE_OF → C (depends on business rule)
```

---

## 🔍 Query Patterns

### 1. Get children

```sql
SELECT * FROM relationships
WHERE from_person_id = :id
AND type = 'PARENT_OF'
```

---

### 2. Get parents

```sql
SELECT * FROM relationships
WHERE to_person_id = :id
AND type = 'PARENT_OF'
```

---

### 3. Get ancestors (recursive)

Using:

* Recursive SQL (CTE), or
* Application traversal (DFS/BFS)

---

## 🌳 Tree Traversal Strategy

### Option 1: Application Layer (recommended first)

* Fetch relationships
* Traverse using DFS/BFS

### Option 2: Database (advanced)

* Recursive CTE
* Graph DB (Neo4j)

---

## ⚖️ Trade-offs

| Approach            | Pros               | Cons           |
| ------------------- | ------------------ | -------------- |
| Graph (this design) | Flexible, scalable | More complex   |
| FK fields           | Simple             | Breaks quickly |

---

## 🚀 Future Enhancements

### 🔹 Add relationship metadata

```text
start_date
end_date
notes
```

### 🔹 Support versioning (history)

### 🔹 Graph optimization

* caching
* precomputed paths

---

## 🧩 Integration with Person Module

```text
Person (Node)
Relationship (Edge)
```

👉 Person stays simple
👉 Relationship handles complexity

---

## 🔥 Key Takeaways

* Genealogy = Graph problem, not relational-only problem
* Avoid fixed columns like `fatherId`
* Use **directed relationship edges**
* Keep domain logic clean and validated

---

## 🚀 Next Steps

* Implement Relationship module (Clean Architecture)
* Add:

    * RelationshipService
    * RelationshipRepository
* Build traversal APIs:

    * `/ancestors`
    * `/descendants`

---
