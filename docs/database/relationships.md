# 👨‍👩‍👧‍👦 Relationship Types & Rules

## 🎯 Overview

The genealogy system uses a graph-based model where relationships between persons are stored as directed edges. Each relationship has a specific type that defines the nature of the connection.

## 🔗 Relationship Types

### PARENT_OF
- **Direction**: Unidirectional (A → B means A is parent of B)
- **Meaning**: A is the biological, adoptive, or step parent of B
- **Storage**: Single relationship record from parent to child
- **Inverse**: CHILD_OF (automatically inferred)
- **Constraints**: Cannot have duplicate parent relationships between same persons

### SPOUSE_OF
- **Direction**: Bidirectional (A ↔ B means A is spouse of B)
- **Meaning**: A and B are married or were married
- **Storage**: Two relationship records (A→B and B→A)
- **Lifecycle**: Uses `start_date` and `end_date` for marriage/divorce
- **Constraints**: Cannot have multiple active spouse relationships per person

### CHILD_OF
- **Direction**: Unidirectional (A → B means A is child of B)
- **Meaning**: A is the child of B (inferred from PARENT_OF)
- **Storage**: Not stored explicitly (computed from PARENT_OF relationships)
- **Note**: This type exists in the enum but is not currently used in storage

### ADOPTED_PARENT_OF
- **Direction**: Unidirectional (A → B means A is adoptive parent of B)
- **Meaning**: A has legally adopted B
- **Storage**: Single relationship record from adoptive parent to child
- **Subtype**: Uses `subtype` field for adoption details

## 📋 Relationship Schema

```sql
CREATE TABLE relationships (
    id BIGSERIAL PRIMARY KEY,
    from_person_id BIGINT NOT NULL,
    to_person_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL, -- PARENT_OF, SPOUSE_OF, etc.
    subtype VARCHAR(20),       -- biological, adopted, step
    start_date DATE,           -- marriage date, birth date
    end_date DATE,             -- divorce date, death date
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📊 Usage Examples

### Creating Parent-Child Relationship

```sql
-- John is parent of Jane
INSERT INTO relationships (from_person_id, to_person_id, type, subtype)
VALUES (1, 2, 'PARENT_OF', 'biological');
```

### Creating Marriage Relationship

```sql
-- John and Mary are married (requires TWO records)
INSERT INTO relationships (from_person_id, to_person_id, type, start_date)
VALUES (1, 3, 'SPOUSE_OF', '2020-06-15');

INSERT INTO relationships (from_person_id, to_person_id, type, start_date)
VALUES (3, 1, 'SPOUSE_OF', '2020-06-15');
```

### Ending Marriage (Divorce)

```sql
-- Update both directions with end_date
UPDATE relationships
SET end_date = '2023-03-10'
WHERE (from_person_id = 1 AND to_person_id = 3 AND type = 'SPOUSE_OF')
   OR (from_person_id = 3 AND to_person_id = 1 AND type = 'SPOUSE_OF');
```

## 🔍 Query Patterns

### Find All Children of a Person

```sql
SELECT p.* FROM persons p
JOIN relationships r ON r.to_person_id = p.id
WHERE r.from_person_id = :personId AND r.type = 'PARENT_OF';
```

### Find Current Spouse

```sql
SELECT p.* FROM persons p
JOIN relationships r ON r.to_person_id = p.id
WHERE r.from_person_id = :personId
  AND r.type = 'SPOUSE_OF'
  AND r.end_date IS NULL;
```

### Find All Relationships for a Person

```sql
SELECT * FROM relationships
WHERE from_person_id = :personId OR to_person_id = :personId;
```

## ⚖️ Business Rules

### 1. Relationship Uniqueness
- **PARENT_OF**: Only one relationship allowed between same parent-child pair
- **SPOUSE_OF**: Only one active relationship allowed per person at a time
- **Self-relationships**: Not allowed (person cannot relate to themselves)

### 2. Bidirectional Requirements
- **SPOUSE_OF**: Must always create both A→B and B→A relationships
- **PARENT_OF**: Only stores one direction (child can infer parent relationship)

### 3. Lifecycle Management
- **SPOUSE_OF**: Use `end_date` for divorce, never delete records
- **PARENT_OF**: Relationships are permanent (adoption, death doesn't remove relationship)

### 4. Data Integrity
- All relationships must reference existing persons
- Relationship types must match the defined enum values
- Dates must be valid (start_date ≤ end_date)

## 🚨 Validation Rules

### API Level Validation
- Person IDs must exist before creating relationships
- Relationship type must be valid enum value
- Cannot create self-relationships
- Cannot create duplicate relationships of same type

### Database Level Constraints
- Foreign key constraints ensure person references are valid
- Unique indexes prevent duplicate relationships
- Check constraints prevent self-relationships

## 🔄 Relationship Inference

Some relationships can be inferred from existing data:

- **CHILD_OF** can be inferred from **PARENT_OF** (inverse direction)
- **SIBLING_OF** can be inferred from shared parents
- **ANCESTOR_OF** uses the person_tree closure table

## 📈 Performance Considerations

- Indexes on `from_person_id`, `to_person_id`, and `type` for fast queries
- Composite unique indexes prevent duplicates
- Closure table (`person_tree`) for efficient ancestor/descendant queries
- Partial indexes for active spouse queries (`end_date IS NULL`)
