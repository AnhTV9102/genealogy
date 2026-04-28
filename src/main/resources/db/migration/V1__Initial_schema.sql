CREATE TABLE persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(10),
    birth_date DATE,
    death_date DATE
);

CREATE TABLE relationships (
    id BIGSERIAL PRIMARY KEY,
    from_person_id BIGINT NOT NULL,
    to_person_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    subtype VARCHAR(20),
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rel_from FOREIGN KEY (from_person_id) REFERENCES persons(id),
    CONSTRAINT fk_rel_to FOREIGN KEY (to_person_id) REFERENCES persons(id)
);

CREATE TABLE person_tree (
    ancestor_id BIGINT NOT NULL,
    descendant_id BIGINT NOT NULL,
    depth INT NOT NULL,
    PRIMARY KEY (ancestor_id, descendant_id),
    CONSTRAINT fk_tree_ancestor FOREIGN KEY (ancestor_id) REFERENCES persons(id),
    CONSTRAINT fk_tree_descendant FOREIGN KEY (descendant_id) REFERENCES persons(id)
);

CREATE INDEX idx_rel_from ON relationships(from_person_id);
CREATE INDEX idx_rel_to ON relationships(to_person_id);
CREATE INDEX idx_rel_type ON relationships(type);

CREATE INDEX idx_tree_ancestor ON person_tree(ancestor_id);
CREATE INDEX idx_tree_descendant ON person_tree(descendant_id);

-- 1. Prevent self relationship (A -> A)
ALTER TABLE relationships
ADD CONSTRAINT chk_no_self_relation
CHECK (from_person_id <> to_person_id);

-- 2. Prevent duplicate parent relationship
CREATE UNIQUE INDEX uniq_parent_relation
ON relationships(from_person_id, to_person_id, type)
WHERE type = 'PARENT_OF';

-- 3. Prevent duplicate active spouse
CREATE UNIQUE INDEX uniq_active_spouse
ON relationships(from_person_id, to_person_id, type)
WHERE type = 'SPOUSE_OF' AND end_date IS NULL;

-- speed up active spouse query
CREATE INDEX idx_spouse_active
ON relationships(from_person_id)
WHERE type = 'SPOUSE_OF' AND end_date IS NULL;
