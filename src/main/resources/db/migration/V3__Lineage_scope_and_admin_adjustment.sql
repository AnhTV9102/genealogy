CREATE TABLE lineages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lineages_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE TABLE lineage_members (
    lineage_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (lineage_id, user_id),
    CONSTRAINT fk_lineage_members_lineage FOREIGN KEY (lineage_id) REFERENCES lineages(id) ON DELETE CASCADE,
    CONSTRAINT fk_lineage_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE lineage_member_permissions (
    lineage_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    PRIMARY KEY (lineage_id, user_id, permission_code),
    CONSTRAINT fk_lineage_member_permissions_member
        FOREIGN KEY (lineage_id, user_id)
        REFERENCES lineage_members(lineage_id, user_id)
        ON DELETE CASCADE
);

-- Restrict global admin permissions to user lifecycle only.
DELETE FROM role_permissions
WHERE role_id = (SELECT id FROM roles WHERE code = 'ROLE_ADMIN');

INSERT INTO permissions (code, description)
VALUES ('USER_CREATE', 'Create user'), ('USER_DELETE', 'Delete user')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('USER_CREATE', 'USER_DELETE')
WHERE r.code = 'ROLE_ADMIN';
