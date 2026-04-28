# 📋 API Overview

## Base URL
```
https://api.genealogy.com/api/v1
```

## Authentication
Currently no authentication required (to be implemented in future phases).

## Response Format
All responses use JSON format with consistent error handling.

### Success Response
```json
{
  "id": 1,
  "name": "John Doe",
  "gender": "MALE",
  "birthDate": "1990-01-01",
  "deathDate": null
}
```

### Error Response
```json
{
  "status": 404,
  "error": "Resource Not Found",
  "message": "Person not found with id: 123",
  "timestamp": "2024-01-01T12:00:00"
}
```

## API Endpoints

### Persons

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/persons` | Create a new person | `CreatePersonRequest` | `PersonResponse` |
| GET | `/persons` | List all persons (paginated) | Query params: `page`, `size`, `sort` | `Page<PersonResponse>` |
| GET | `/persons/{id}` | Get person by ID | - | `PersonResponse` |
| PUT | `/persons/{id}` | Update person | `UpdatePersonRequest` | `PersonResponse` |
| DELETE | `/persons/{id}` | Delete person | - | `204 No Content` |

### Relationships

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/relationships` | Create a relationship | `CreateRelationshipRequest` | `RelationshipResponse` |
| GET | `/relationships/{id}` | Get relationship by ID | - | `RelationshipResponse` |
| DELETE | `/relationships/{id}` | Delete relationship | - | `204 No Content` |

### Person Relationships

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/persons/{personId}/relationships` | Get all relationships for a person | - | `List<RelationshipResponse>` |
| GET | `/persons/{personId}/parents` | Get parents of a person | - | `List<PersonResponse>` |
| GET | `/persons/{personId}/children` | Get children of a person | - | `List<PersonResponse>` |
| GET | `/persons/{personId}/spouse` | Get spouse of a person | - | `PersonResponse` or `204 No Content` |
| GET | `/persons/{personId}/ancestors` | Get ancestors (paginated by generations) | Query param: `generations` | `List<PersonResponse>` |
| GET | `/persons/{personId}/descendants` | Get descendants (paginated by generations) | Query param: `generations` | `List<PersonResponse>` |

## Request/Response Schemas

### CreatePersonRequest
```json
{
  "fullName": "string (required, 1-255 chars)",
  "gender": "string (required, MALE/FEMALE/OTHER)",
  "dateOfBirth": "date (required, past date)"
}
```

### UpdatePersonRequest
```json
{
  "fullName": "string (optional, 1-255 chars)",
  "dateOfDeath": "date (optional, not future)"
}
```

### CreateRelationshipRequest
```json
{
  "fromPersonId": "number (required, positive)",
  "toPersonId": "number (required, positive)",
  "type": "string (required, PARENT_OF/CHILD_OF/SPOUSE_OF/ADOPTED_PARENT_OF)"
}
```

### PersonResponse
```json
{
  "id": "number",
  "fullName": "string",
  "gender": "string",
  "birthDate": "date",
  "deathDate": "date"
}
```

### RelationshipResponse
```json
{
  "id": "number",
  "fromPersonId": "number",
  "toPersonId": "number",
  "type": "string",
  "subtype": "string",
  "startDate": "date",
  "endDate": "date",
  "createdAt": "datetime"
}
```

## HTTP Status Codes

- `200 OK` - Successful GET/PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests or empty results
- `400 Bad Request` - Validation errors or business rule violations
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Unexpected server errors

## Validation Rules

### Person Validation
- Full name: Required, 1-255 characters
- Gender: Required, must be valid enum value (case-insensitive)
- Date of birth: Required, must be in the past
- Date of death: Optional, cannot be in the future

### Relationship Validation
- From/To person IDs: Required, positive numbers, persons must exist
- Type: Required, must be valid RelationshipType enum value (case-insensitive)
- Cannot create self-relationships (fromPersonId ≠ toPersonId)
- Duplicate relationships are prevented based on type

## Pagination

List endpoints support pagination:
- `page` (default: 0) - Page number (0-based)
- `size` (default: 20) - Page size
- `sort` (default: id,asc) - Sort field and direction

Response includes pagination metadata:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "field": "id",
      "direction": "ASC"
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

## Error Handling

The API uses structured error responses with appropriate HTTP status codes:

- **Resource Not Found (404)**: When requested entity doesn't exist
- **Bad Request (400)**: Validation failures or business rule violations
- **Internal Server Error (500)**: Unexpected system errors

All error responses include timestamp and descriptive messages.
