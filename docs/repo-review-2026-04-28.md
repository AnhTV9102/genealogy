# Repository Review (Coding, `docs/`, `database/`)

Date: 2026-04-28  
Scope reviewed: Java source code, documentation under `docs/`, and SQL/database assets under `database/`.

## Executive Summary

The project has a clear modular structure (person/relationship domains, layered architecture, and a useful documentation tree), but there are several correctness and maintainability gaps:

1. **Data consistency risk between code and SQL constraints** (relationship `type` values do not consistently match across code/docs/init scripts).
2. **Service-layer and API validation gaps** (generic exceptions, missing request validation annotations, and tight coupling to infra implementation classes).
3. **Documentation drift/incompleteness** (empty ERD doc and schema contradictions).

## 1) Coding Review

### Strengths

- The package layout follows a domain-oriented separation (`person`, `relationship`) with clear API/application/domain/infrastructure boundaries.
- Domain models (`Person`, `Relationship`) encapsulate business invariants (e.g., self-relationship prevention and date consistency checks).

### Findings

#### C1 — Service layer depends on infrastructure implementation directly (**Medium**)

- `PersonService` injects both `PersonRepository` and `PersonRepositoryImpl`.
- This breaks the abstraction boundary and makes the service depend on an infrastructure class for pagination.

**Evidence:** `src/main/java/com/genealogy/person/application/service/PersonService.java`.

**Recommendation:**
- Add paginated read method to `PersonRepository` interface and implement it in infra.
- Remove direct dependency on `PersonRepositoryImpl` from application service.

---

#### C2 — Generic `RuntimeException` used for domain/application errors (**Medium**)

- Multiple flows throw raw `RuntimeException` for not-found and duplicate conditions.
- This leads to poor API error semantics and inconsistent HTTP response mapping.

**Evidence:**
- `src/main/java/com/genealogy/person/application/service/PersonService.java`
- `src/main/java/com/genealogy/relationship/application/service/RelationshipService.java`

**Recommendation:**
- Introduce typed exceptions (e.g., `PersonNotFoundException`, `RelationshipAlreadyExistsException`) and a `@ControllerAdvice` to map errors to explicit statuses.

---

#### C3 — Missing request validation on controllers (**Medium**)

- DTOs are accepted without `@Valid` in controller method parameters.
- DTOs currently have no validation constraints either (`@NotBlank`, `@NotNull`, etc.).

**Evidence:**
- `src/main/java/com/genealogy/person/api/PersonController.java`
- `src/main/java/com/genealogy/relationship/api/RelationshipController.java`
- DTOs under `src/main/java/com/genealogy/**/application/dto/`

**Recommendation:**
- Add bean validation annotations to request records.
- Add `@Valid` to controller request bodies.

---

#### C4 — Potential enum/value mismatch in relationship duplicate check (**High**)

- In `RelationshipService#create`, relationship type is normalized to uppercase for enum parsing, but duplicate check uses raw `request.type()`.
- If persisted values are uppercase enum names, mixed-case input may bypass duplicate checks.

**Evidence:** `src/main/java/com/genealogy/relationship/application/service/RelationshipService.java`.

**Recommendation:**
- Use normalized enum name consistently for persistence and duplicate checks (`type.name()`).

---

#### C5 — Placeholder/example classes left in main sources (**Low**)

- `config/Example.java`, `security/Example.java`, and `common/example.java` appear to be placeholders.
- `common/example.java` also violates Java naming convention (class name should be `Example`).

**Evidence:**
- `src/main/java/com/genealogy/config/Example.java`
- `src/main/java/com/genealogy/security/Example.java`
- `src/main/java/com/genealogy/common/example.java`

**Recommendation:**
- Remove placeholders or replace with real implementations.
- Rename `example` class/file to `Example` if retained.

## 2) Documentation Review (`docs/`)

### Findings

#### D1 — `docs/database/erd.md` is empty (**Medium**)

- The file exists but has no content.

**Evidence:** `docs/database/erd.md` (0 lines).

**Recommendation:**
- Add ERD explanation and align with `docs/diagrams/erd.mmd`.

---

#### D2 — Schema docs conflict with SQL init script on relationship type literals (**High**)

- `docs/database/schema.md` uses uppercase literals (`PARENT_OF`, `SPOUSE_OF`) in filtered indexes.
- `database/init/init.sql` currently defines filtered indexes with lowercase literals (`parent`, `spouse`).

**Evidence:**
- `docs/database/schema.md`
- `database/init/init.sql`

**Recommendation:**
- Standardize relationship type vocabulary across domain enums, SQL constraints/indexes, and API docs.

---

#### D3 — API/docs alignment should be explicitly versioned (**Low**)

- API controllers expose `/api/v1/...` paths, but docs could more explicitly state versioning and canonical path list in one place.

**Evidence:**
- `src/main/java/com/genealogy/person/api/PersonController.java`
- `src/main/java/com/genealogy/relationship/api/RelationshipController.java`
- `docs/api/overview.md`

**Recommendation:**
- Add a single authoritative endpoint matrix (path/method/request/response/errors) in `docs/api/overview.md`.

## 3) Database Review (`database/`)

### Findings

#### DB1 — Type literal mismatch can invalidate uniqueness constraints (**High**)

- Partial unique indexes in `init.sql` rely on lowercase type values, while application queries and enums use uppercase enum-like values (`PARENT_OF`, `SPOUSE_OF`).
- If actual rows store uppercase values, those partial unique indexes will not apply as intended.

**Evidence:**
- `database/init/init.sql`
- `src/main/java/com/genealogy/relationship/infrastructure/persistence/repository/JpaRelationshipRepository.java`
- `src/main/java/com/genealogy/relationship/domain/model/RelationshipType.java`

**Recommendation:**
- Align DB constraints/index filters with persisted canonical enum values.
- Consider CHECK constraints for valid `type` domain.

---

#### DB2 — Flyway dependency present but migration layout not wired yet (**Medium**)

- `build.gradle.kts` includes Flyway, but repo primarily shows `database/init/init.sql` and `database/migrations/example.md` (documentation/examples).
- No obvious active versioned migration scripts under standard runtime location (`src/main/resources/db/migration`).

**Evidence:**
- `build.gradle.kts`
- `database/init/init.sql`
- `database/migrations/example.md`

**Recommendation:**
- Move authoritative schema changes into versioned Flyway migrations and keep `init.sql` synchronized or clearly marked as dev bootstrap only.

## Suggested Priority Backlog

1. **Fix relationship type canonicalization end-to-end** (C4, D2, DB1).
2. **Introduce structured exception handling + validation** (C2, C3).
3. **Remove infra leak in service dependency** (C1).
4. **Complete/clean docs and placeholders** (D1, C5, D3).
5. **Formalize migration strategy with Flyway** (DB2).

## Notes on Verification

- Attempted to run test suite via Gradle wrapper, but wrapper download was blocked by network/proxy restrictions in this environment.
