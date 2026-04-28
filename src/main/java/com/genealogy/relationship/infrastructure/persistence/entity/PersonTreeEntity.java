package com.genealogy.relationship.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity for person_tree table (Closure Table pattern).
 * This table stores ancestor-descendant relationships for efficient tree traversal.
 */
@Entity
@Table(name = "person_tree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PersonTreeEntityId.class)
public class PersonTreeEntity {

    @Id
    @Column(name = "ancestor_id", nullable = false)
    private Long ancestorId;

    @Id
    @Column(name = "descendant_id", nullable = false)
    private Long descendantId;

    @Column(nullable = false)
    private Integer depth;
}

