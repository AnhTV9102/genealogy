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

## 🔗 person_relationships

### Schema

```sql
person_relationships (
    id BIGINT PRIMARY KEY,

    from_person_id BIGINT NOT NULL,
    to_person_id BIGINT NOT NULL,

    type VARCHAR(20) NOT NULL, 
    -- parent, spouse

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
| parent | A → B = A is parent of B |
| spouse | A ↔ B                    |

---

### Rules

#### 1. Direction

* `parent`: directional
* `spouse`: must store **both directions**

---

#### 2. Do NOT delete

* Divorce → set `end_date`
* Keep history

---

#### 3. subtype usage

Only for `parent`:

* biological
* adopted
* step

---

#### 4. Data consistency

* Avoid duplicate relationships
* Ensure both spouse directions exist

---

## 🌳 person_tree (Closure Table)

### Schema

```sql
person_tree (
    ancestor_id BIGINT NOT NULL,
    descendant_id BIGINT NOT NULL,
    depth INT NOT NULL,

    PRIMARY KEY (ancestor_id, descendant_id)
);
```

---

### Description

Stores precomputed ancestor-descendant relationships for fast queries.

---

### Depth Meaning

| depth | meaning           |
| ----- | ----------------- |
| 0     | self              |
| 1     | parent            |
| 2     | grandparent       |
| 3     | great-grandparent |

---

### Rules

#### 1. Self record (REQUIRED)

Every person must have:

```
(A, A, 0)
```

---

#### 2. Only parent hierarchy

* Do NOT store spouse relationships

---

#### 3. Must be updated when adding parent

Failure to sync will break queries.

---

# ⚙️ Data Flow

---

## Add Person

```sql
INSERT INTO persons (...);
```

Then:

```sql
INSERT INTO person_tree (ancestor_id, descendant_id, depth)
VALUES (:id, :id, 0);
```

---

## Add Parent Relationship

### Step 1: Insert relationship

```sql
INSERT INTO person_relationships (...);
```

---

### Step 2: Update tree

```sql
-- Direct parent
INSERT INTO person_tree (ancestor_id, descendant_id, depth)
VALUES (:parentId, :childId, 1);

-- Inherit ancestors
INSERT INTO person_tree (ancestor_id, descendant_id, depth)
SELECT ancestor_id, :childId, depth + 1
FROM person_tree
WHERE descendant_id = :parentId;
```

---

# 📊 Queries

---

## 👶 Get Children

```sql
SELECT p.*
FROM person_relationships r
JOIN persons p ON r.to_person_id = p.id
WHERE r.from_person_id = :id
AND r.type = 'parent';
```

---

## 👴 Get Parents

```sql
SELECT p.*
FROM person_relationships r
JOIN persons p ON r.from_person_id = p.id
WHERE r.to_person_id = :id
AND r.type = 'parent';
```

---

## ❤️ Get Current Spouse

```sql
SELECT p.*
FROM person_relationships r
JOIN persons p ON r.to_person_id = p.id
WHERE r.from_person_id = :id
AND r.type = 'spouse'
AND r.end_date IS NULL;
```

---

## 🌳 Get Descendants

```sql
SELECT p.*
FROM person_tree t
JOIN persons p ON t.descendant_id = p.id
WHERE t.ancestor_id = :id
AND t.depth > 0;
```

---

## 🌳 Get Ancestors

```sql
SELECT p.*
FROM person_tree t
JOIN persons p ON t.ancestor_id = p.id
WHERE t.descendant_id = :id
AND t.depth > 0;
```

---

# 🚨 Common Pitfalls

* Missing `(A, A, 0)` in `person_tree`
* Not updating `person_tree` after insert
* Storing only one direction for spouse
* Deleting relationships instead of ending them
* Using `father/mother` instead of `parent`

---

# ⚡ Index Recommendations

```sql
CREATE INDEX idx_rel_from ON person_relationships(from_person_id);
CREATE INDEX idx_rel_to ON person_relationships(to_person_id);

CREATE INDEX idx_tree_ancestor ON person_tree(ancestor_id);
CREATE INDEX idx_tree_descendant ON person_tree(descendant_id);
```

---

# 🚀 Future Extensions

* events (birth, marriage, death)
* media (photos)
* audit logs

---

# 🎯 Philosophy

> Keep it simple.
> Add complexity only when necessary.
