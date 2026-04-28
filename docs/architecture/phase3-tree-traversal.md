# 🌳 Phase 3: Tree Traversal Implementation

## 🎯 Overview

Phase 3 implements the **tree traversal** functionality to efficiently query ancestors and descendants using the **Closure Table Pattern**.

## 📊 Architecture Components

### 1. Domain Layer

#### `PersonTree` Domain Model
```java
public class PersonTree {
    private final Long ancestorId;
    private final Long descendantId;
    private final Integer depth;
}
```

- Represents a single ancestor-descendant relationship
- `depth` indicates generations (0=self, 1=parent/child, 2=grandparent/grandchild, etc.)
- Immutable design ensures data consistency

#### `PersonTreeRepository` Interface
```java
public interface PersonTreeRepository {
    List<PersonTree> findAncestors(Long personId, Integer maxDepth);
    List<PersonTree> findDescendants(Long personId, Integer maxDepth);
    List<PersonTree> findAncestorsGenerations(Long personId, int generations);
    List<PersonTree> findDescendantsGenerations(Long personId, int generations);
    Optional<PersonTree> findByAncestorAndDescendant(Long ancestorId, Long descendantId);
    PersonTree save(PersonTree personTree);
    void delete(Long ancestorId, Long descendantId);
}
```

### 2. Infrastructure Layer

#### `PersonTreeEntity` (JPA Entity)
- Maps to `person_tree` table
- Uses composite primary key: `(ancestor_id, descendant_id)`

#### `PersonTreeEntityId` (Composite Key)
- Implements `Serializable`
- Contains both ancestor and descendant IDs

#### `JpaPersonTreeRepository` (Spring Data Repository)
```java
@Query("SELECT pt FROM PersonTreeEntity pt WHERE pt.descendantId = :personId AND pt.depth > 0 ORDER BY pt.depth ASC")
List<PersonTreeEntity> findAncestors(@Param("personId") Long personId);

@Query("SELECT pt FROM PersonTreeEntity pt WHERE pt.ancestorId = :personId AND pt.depth > 0 ORDER BY pt.depth ASC")
List<PersonTreeEntity> findDescendants(@Param("personId") Long personId);
```

#### `PersonTreeRepositoryImpl` (Infrastructure Implementation)
- Converts JPA entities to domain models
- Handles optional depth filtering

### 3. Application Layer

#### Enhanced `RelationshipService`
```java
public List<PersonResponse> getAncestors(Long personId, Integer generations);
public List<PersonResponse> getDescendants(Long personId, Integer generations);
```

- Validates person existence
- Queries person tree using repository
- Converts results to PersonResponse DTOs
- Maintains generation order (closest ancestors/descendants first)

### 4. API Layer

#### `PersonController` Endpoints

**GET /api/v1/persons/{personId}/ancestors**
- Query parameter: `generations` (optional, defaults to all)
- Returns: `List<PersonResponse>` ordered by generation
- Excludes self-relationships (depth > 0)

**GET /api/v1/persons/{personId}/descendants**
- Query parameter: `generations` (optional, defaults to all)
- Returns: `List<PersonResponse>` ordered by generation
- Excludes self-relationships (depth > 0)

## 🗂️ File Structure

```
relationship/
├── domain/
│   ├── model/
│   │   ├── Relationship.java
│   │   ├── RelationshipType.java
│   │   └── PersonTree.java (NEW)
│   └── repository/
│       ├── RelationshipRepository.java
│       └── PersonTreeRepository.java (NEW)
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── RelationshipEntity.java
│   │   │   ├── PersonTreeEntity.java (NEW)
│   │   │   └── PersonTreeEntityId.java (NEW)
│   │   ├── repository/
│   │   │   ├── RelationshipRepositoryImpl.java
│   │   │   ├── PersonTreeRepositoryImpl.java (NEW)
│   │   │   ├── JpaRelationshipRepository.java
│   │   │   └── JpaPersonTreeRepository.java (NEW)
│   │   └── mapper/
│   │       ├── RelationshipPersistenceMapper.java
│   │       └── PersonTreePersistenceMapper.java (NEW)
└── application/
    └── service/
        └── RelationshipService.java (UPDATED)

person/
└── api/
    └── PersonController.java (UPDATED - added 2 new endpoints)
```

## 🔄 Closure Table Pattern

### Database Schema

```sql
CREATE TABLE person_tree (
    ancestor_id BIGINT NOT NULL,
    descendant_id BIGINT NOT NULL,
    depth INT NOT NULL,
    PRIMARY KEY (ancestor_id, descendant_id),
    CONSTRAINT fk_tree_ancestor FOREIGN KEY (ancestor_id) REFERENCES persons(id),
    CONSTRAINT fk_tree_descendant FOREIGN KEY (descendant_id) REFERENCES persons(id)
);

CREATE INDEX idx_tree_ancestor ON person_tree(ancestor_id);
CREATE INDEX idx_tree_descendant ON person_tree(descendant_id);
```

### Why Closure Table?

| Method | Query Complexity | Storage | Update |
|--------|------------------|---------|--------|
| **Closure Table** | O(1) ✅ | O(n²) in worst case | O(n) |
| Adjacency List | O(h) | O(n) | O(1) |
| Nested Sets | O(1) | O(n) | O(n) |

For genealogy systems, Closure Table is ideal because:
1. Queries are **fast** (no recursion needed)
2. Filtering by generation is **trivial** (just check depth)
3. Updates are tolerable (only when creating new relationships)

### Data Maintenance Rules

1. **Every new person** must have self-entry: `(personId, personId, 0)`
2. **After creating parent relationship** A→B:
   - Add direct entry: `(A, B, 1)`
   - Inherit ancestors: For each `(X, A, d)` in person_tree, add `(X, B, d+1)`
3. **Queries always exclude self** (`depth > 0`)

## 📋 API Usage Examples

### Get Immediate Parents (depth 1)
```bash
curl -X GET "http://localhost:8080/api/v1/persons/123/ancestors?generations=1"
```

Response:
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "gender": "male",
    "birthDate": "1960-01-15"
  },
  {
    "id": 2,
    "name": "Jane Doe",
    "gender": "female",
    "birthDate": "1962-03-20"
  }
]
```

### Get All Descendants (unlimited depth)
```bash
curl -X GET "http://localhost:8080/api/v1/persons/123/descendants"
```

### Get Grandchildren Only (depth 2)
```bash
curl -X GET "http://localhost:8080/api/v1/persons/123/descendants?generations=2"
```

## 🧪 Testing Scenarios

### Scenario 1: Three-Generation Family
```
A (1920)
├─ B (1945)
│  ├─ C (1970)
│  │  └─ D (1995)
│  └─ E (1972)
└─ F (1948)
   └─ G (1975)
```

**Query person C's ancestors:**
- Results: B (depth 1), A (depth 2)
- With `generations=1`: Only B

**Query person A's descendants:**
- Results: B, F, C, E, G, D
- With `generations=2`: B, F, C, E, G
- With `generations=1`: B, F

## 🚀 Performance Considerations

### Query Optimization

1. **Ordering by depth**: Queries explicitly order by depth for generation order
2. **Index usage**: Both `idx_tree_ancestor` and `idx_tree_descendant` are used
3. **No N+1 queries**: PersonTree IDs are used to fetch Person objects in single lookup

### Scalability

- **Storage**: O(n²) in worst case (complete tree), typical genealogy ~O(n log n)
- **Query time**: O(1) per relationship + O(p) for batch person lookup
- **Update time**: O(a) where a is number of existing ancestors (when creating relationships)

## 🔮 Future Enhancements

1. **Sibling queries**: `getSiblings(personId)` - infer from shared parents
2. **Generation names**: API to get "Grandparents", "Uncles/Aunts", etc.
3. **Consanguinity calculation**: Determine relationship type between any two people
4. **Tree visualization**: Export tree structure for frontend rendering
5. **Relationship validation**: Check for invalid family structures (cycles, conflicts)

## 📚 References

- [Closure Table Pattern](https://en.wikipedia.org/wiki/Closure_table)
- [SQL Tree Structures](https://www.slideshare.net/billkarwin/models-for-hierarchical-data)
- [Entity Relationships in Java](https://www.baeldung.com/jpa-composite-primary-keys)

