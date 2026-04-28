# 🔗 Phase 3 API Integration Guide

## 📌 Quick Start

### Prerequisites
- Backend running on `http://localhost:8080`
- Database with some persons and relationships

### Base URL
```
http://localhost:8080/api/v1
```

---

## 🚀 Two New Endpoints

### 1️⃣ Get Ancestors

**Endpoint:** `GET /api/v1/persons/{personId}/ancestors`

**Parameters:**
- `personId` (path, required): The person ID
- `generations` (query, optional): Number of generations to retrieve (1, 2, 3, etc.)

**Query Examples:**

```bash
# Get all ancestors (unlimited generations)
curl -X GET "http://localhost:8080/api/v1/persons/5/ancestors"

# Get immediate parents only (generation 1)
curl -X GET "http://localhost:8080/api/v1/persons/5/ancestors?generations=1"

# Get parents and grandparents (2 generations)
curl -X GET "http://localhost:8080/api/v1/persons/5/ancestors?generations=2"
```

**Response:**
```json
[
  {
    "id": 2,
    "name": "Mary Johnson",
    "gender": "female",
    "birthDate": "1960-03-15",
    "deathDate": null
  },
  {
    "id": 1,
    "name": "John Johnson",
    "gender": "male",
    "birthDate": "1958-07-22",
    "deathDate": null
  }
]
```

**Status Codes:**
- `200 OK` - Ancestors found
- `404 NOT_FOUND` - Person not found
- `500 INTERNAL_SERVER_ERROR` - Server error

---

### 2️⃣ Get Descendants

**Endpoint:** `GET /api/v1/persons/{personId}/descendants`

**Parameters:**
- `personId` (path, required): The person ID
- `generations` (query, optional): Number of generations to retrieve (1, 2, 3, etc.)

**Query Examples:**

```bash
# Get all descendants (unlimited generations)
curl -X GET "http://localhost:8080/api/v1/persons/1/descendants"

# Get immediate children only (generation 1)
curl -X GET "http://localhost:8080/api/v1/persons/1/descendants?generations=1"

# Get children and grandchildren (2 generations)
curl -X GET "http://localhost:8080/api/v1/persons/1/descendants?generations=2"
```

**Response:**
```json
[
  {
    "id": 2,
    "name": "Alice Smith",
    "gender": "female",
    "birthDate": "1985-05-10",
    "deathDate": null
  },
  {
    "id": 3,
    "name": "Bob Smith",
    "gender": "male",
    "birthDate": "1990-02-20",
    "deathDate": null
  },
  {
    "id": 4,
    "name": "Charlie Smith",
    "gender": "male",
    "birthDate": "2015-11-05",
    "deathDate": null
  }
]
```

**Status Codes:**
- `200 OK` - Descendants found
- `404 NOT_FOUND` - Person not found
- `500 INTERNAL_SERVER_ERROR` - Server error

---

## 📊 Understanding Generation Ordering

Results are **always sorted by generation** (closest relations first):

### Example: Get Person 1's Descendants (All Generations)
```
Person 1 
├── Child A (generation 1)
├── Child B (generation 1)
│   └── Grandchild X (generation 2)
│   └── Grandchild Y (generation 2)
└── Child C (generation 1)
    └── Grandchild Z (generation 2)
```

**API Response Order:**
1. Child A
2. Child B  
3. Child C
4. Grandchild X
5. Grandchild Y
6. Grandchild Z

*(All children first, then all grandchildren)*

---

## 🔄 Combining APIs

### Build Family Tree from Top-Down

```bash
# Step 1: Get root person
GET /api/v1/persons/1

# Step 2: Get their children
GET /api/v1/persons/1/descendants?generations=1
# Response: [Alice, Bob, Charlie]

# Step 3: For each child, get their children
GET /api/v1/persons/{alice_id}/descendants?generations=1
GET /api/v1/persons/{bob_id}/descendants?generations=1
GET /api/v1/persons/{charlie_id}/descendants?generations=1
```

### Build Family Tree from Bottom-Up

```bash
# Step 1: Get a person (leaf node)
GET /api/v1/persons/10

# Step 2: Get their ancestors
GET /api/v1/persons/10/ancestors
# Response: [Parent, Grandparent, Great-Grandparent, ...]

# Step 3: For each ancestor, get their siblings (via shared parents)
GET /api/v1/persons/{parent_id}/descendants?generations=1
```

---

## 💡 Practical Use Cases

### Use Case 1: Get All Family Members of Person A

```bash
# 1. Get ancestors (parents, grandparents, etc.)
ANCESTORS=$(GET /api/v1/persons/123/ancestors)

# 2. Get descendants (children, grandchildren, etc.)
DESCENDANTS=$(GET /api/v1/persons/123/descendants)

# 3. Combine and display family tree
```

### Use Case 2: Find Siblings

```bash
# 1. Get parents (immediate ancestors, generation=1)
PARENTS=$(GET /api/v1/persons/123/ancestors?generations=1)

# 2. For each parent, get their children (generation=1)
SIBLINGS=$(GET /api/v1/persons/{parent_id}/descendants?generations=1)

# 3. Exclude the person themselves from siblings
```

### Use Case 3: Count Generations

```bash
# Get all ancestors
ANCESTORS=$(GET /api/v1/persons/456/ancestors)

# Number of ancestors = number of generations
GENERATIONS=$(count(ANCESTORS))
```

### Use Case 4: Display Family Statistics

```bash
# 1. Get all descendants
ALL_DESCENDANTS=$(GET /api/v1/persons/1/descendants)

# 2. Get children only
CHILDREN=$(GET /api/v1/persons/1/descendants?generations=1)

# 3. Get grandchildren only (by filtering descendants by generation)
GRANDCHILDREN=$(GET /api/v1/persons/1/descendants?generations=2) - CHILDREN
```

---

## 🐛 Error Handling

### Person Not Found
```json
{
  "error": "Person not found with id: 999",
  "status": 404,
  "timestamp": "2026-04-28T08:00:00Z"
}
```

### Invalid Request
```json
{
  "error": "Invalid request parameter",
  "status": 400,
  "details": "generations must be a positive integer"
}
```

---

## ⚙️ How It Works Internally

### Closure Table Lookup

1. **Query:** `/api/v1/persons/5/ancestors?generations=2`
2. **SQL:** 
   ```sql
   SELECT * FROM person_tree 
   WHERE descendant_id = 5 
   AND depth > 0 AND depth <= 2
   ORDER BY depth ASC
   ```
3. **Results:** `(1,5,2), (2,5,1)` → Person 1 and 2
4. **Response:** PersonResponse for persons 1 and 2

### Performance

- **Complexity:** O(1) lookup in person_tree table
- **Indices:** Uses `idx_tree_descendant` and `idx_tree_ancestor`
- **N+1 Prevention:** Batch fetch of Person objects after tree lookup

---

## 📱 Frontend Integration Examples

### React Hook Example

```typescript
interface PersonResponse {
  id: number;
  name: string;
  gender: string;
  birthDate: string;
  deathDate?: string;
}

// Get ancestors
const getAncestors = async (personId: number, generations?: number) => {
  const params = generations ? `?generations=${generations}` : '';
  const response = await fetch(
    `/api/v1/persons/${personId}/ancestors${params}`
  );
  return response.json() as Promise<PersonResponse[]>;
};

// Get descendants
const getDescendants = async (personId: number, generations?: number) => {
  const params = generations ? `?generations=${generations}` : '';
  const response = await fetch(
    `/api/v1/persons/${personId}/descendants${params}`
  );
  return response.json() as Promise<PersonResponse[]>;
};
```

### Build Family Tree Component

```typescript
const [ancestors, setAncestors] = useState<PersonResponse[]>([]);
const [descendants, setDescendants] = useState<PersonResponse[]>([]);

useEffect(() => {
  const loadFamilyTree = async () => {
    setAncestors(await getAncestors(personId));
    setDescendants(await getDescendants(personId));
  };
  loadFamilyTree();
}, [personId]);
```

---

## 📝 Notes

- All responses are paginated by **generation (depth)**
- Self-relationships are **excluded** from results
- Results are **ordered by generation** (closest first)
- The API is **read-only** (GET requests)
- Uses **Closure Table Pattern** for efficient queries
- Supports **generation limiting** via optional parameter

---

## 🔗 Related APIs

### Phase 2 - Family Relationships

```bash
# Get immediate parents
GET /api/v1/persons/{personId}/parents

# Get immediate children
GET /api/v1/persons/{personId}/children

# Get current spouse
GET /api/v1/persons/{personId}/spouse

# Get all relationships
GET /api/v1/persons/{personId}/relationships
```

### Phase 1 - Person Management

```bash
# Create person
POST /api/v1/persons

# Get person
GET /api/v1/persons/{id}

# Update person
PUT /api/v1/persons/{id}

# Delete person
DELETE /api/v1/persons/{id}

# List all persons
GET /api/v1/persons?page=0&size=10
```

---

## 🆘 Troubleshooting

### "Person not found" even though they exist
- Check the person ID is correct
- Verify the person is in the database

### No ancestors/descendants returned
- Person may not have any relationships set up
- Person relationships must be created via `/api/v1/relationships` first
- Check that `person_tree` table is populated (auto-updated on relationship creation)

### Unexpected ordering
- Results are always sorted by `depth` (generation)
- Persons at same generation may be in any order
- Exclude self-relationships (depth > 0)

### Performance Issues with Large Trees
- Use `generations` parameter to limit results
- The Closure Table pattern is optimized for read-heavy workloads
- Index usage is automatic via JPA queries

