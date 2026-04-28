# 📋 Database Migration Guide

## 🎯 Overview

This guide explains how to create and manage database migrations for the Genealogy system using Flyway.

## 📁 Migration File Structure

```
database/migrations/
├── V1__Initial_schema.sql          # Initial database setup
├── V2__Add_audit_fields.sql        # Add created_at, updated_at
├── V3__Create_indexes.sql          # Performance improvements
└── V4__Add_relationship_subtype.sql # New relationship features
```

## 📝 Migration Naming Convention

Flyway uses the following naming pattern:
- **V{version}__{description}.sql**
- Version: Numeric, incremental (1, 2, 3, etc.)
- Description: Human-readable, underscore-separated

## 🔧 Creating a New Migration

### Step 1: Create Migration File

```bash
# Create a new migration file
touch database/migrations/V5__Add_person_notes.sql
```

### Step 2: Write Migration SQL

```sql
-- V5__Add_person_notes.sql
ALTER TABLE persons ADD COLUMN notes TEXT;

-- Add index for search performance
CREATE INDEX idx_persons_notes ON persons USING gin(to_tsvector('english', notes));
```

### Step 3: Test Migration

```bash
# Run migrations
./gradlew flywayMigrate

# Validate
./gradlew flywayValidate
```

## ✅ Migration Best Practices

### 1. Make Migrations Idempotent
- Migrations should be safe to run multiple times
- Use `IF NOT EXISTS` for CREATE statements
- Check for column existence before adding

### 2. Keep Migrations Small
- One logical change per migration
- Easier to troubleshoot and rollback

### 3. Test Thoroughly
- Test on development database first
- Verify data integrity after migration
- Test rollback procedures

### 4. Document Changes
- Comment your SQL clearly
- Update schema documentation
- Note any data transformations

## 🔄 Migration Types

### Schema Changes
```sql
-- Adding a column
ALTER TABLE persons ADD COLUMN middle_name VARCHAR(100);

-- Creating indexes
CREATE INDEX idx_relationships_type ON relationships(type);

-- Adding constraints
ALTER TABLE relationships ADD CONSTRAINT chk_valid_dates
CHECK (start_date <= end_date);
```

### Data Migrations
```sql
-- Update existing data
UPDATE persons SET gender = 'unknown' WHERE gender IS NULL;

-- Transform data
UPDATE relationships SET subtype = 'biological'
WHERE type = 'PARENT_OF' AND subtype IS NULL;
```

### Refactoring
```sql
-- Rename column (requires careful planning)
ALTER TABLE persons RENAME COLUMN name TO full_name;

-- Change data types
ALTER TABLE persons ALTER COLUMN birth_date TYPE TIMESTAMP;
```

## 🚨 Rollback Strategy

### Option 1: Down Migrations (Not Recommended)
Flyway doesn't support down migrations by default. Create separate rollback scripts if needed.

### Option 2: Compensating Migrations
Create a new migration that undoes the previous change:

```sql
-- V6__Remove_person_notes.sql (compensates V5)
DROP INDEX IF EXISTS idx_persons_notes;
ALTER TABLE persons DROP COLUMN IF EXISTS notes;
```

### Option 3: Data Backup
Always backup data before running migrations that modify existing data.

## 📊 Monitoring Migrations

### Check Migration Status
```sql
-- View applied migrations
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Common Issues
- **Version conflicts**: Ensure version numbers are unique
- **Failed migrations**: Check logs, fix issues, then re-run
- **Out-of-order**: Migrations must be applied in version order

## 🛠️ Flyway Commands

```bash
# Migrate to latest version
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo

# Validate migrations
./gradlew flywayValidate

# Clean database (development only!)
./gradlew flywayClean
```

## 📋 Current Migration History

| Version | Description | Date | Status |
|---------|-------------|------|--------|
| V1 | Initial schema with persons and relationships tables | 2024-01-XX | ✅ Applied |
| V2 | Add person_tree closure table | 2024-01-XX | ✅ Applied |
| V3 | Create performance indexes | 2024-01-XX | ✅ Applied |
| V4 | Add data integrity constraints | 2024-01-XX | ✅ Applied |

## 🔍 Troubleshooting

### Migration Fails
1. Check the exact error message
2. Verify SQL syntax
3. Ensure referenced objects exist
4. Test on a copy of production data

### Data Loss Prevention
1. Always backup before migration
2. Test on staging environment first
3. Have rollback plan ready
4. Monitor application during deployment

### Performance Impact
1. Schedule migrations during low-traffic periods
2. Monitor database performance during migration
3. Consider table locking implications
4. Use batch operations for large datasets
