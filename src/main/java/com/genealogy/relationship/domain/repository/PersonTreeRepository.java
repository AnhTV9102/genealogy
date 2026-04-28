package com.genealogy.relationship.domain.repository;

import com.genealogy.relationship.domain.model.PersonTree;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PersonTree (Closure Table) queries.
 * This provides efficient ancestor/descendant traversal using the closure table pattern.
 */
public interface PersonTreeRepository {
    /**
     * Save a person tree relationship.
     */
    PersonTree save(PersonTree personTree);

    /**
     * Find all ancestors of a person, ordered by depth (closest ancestors first).
     *
     * @param personId the person ID
     * @param maxDepth optional maximum depth (null for all), excludes self (depth=0)
     * @return list of ancestors ordered by depth ascending
     */
    List<PersonTree> findAncestors(Long personId, Integer maxDepth);

    /**
     * Find all descendants of a person, ordered by depth (closest descendants first).
     *
     * @param personId the person ID
     * @param maxDepth optional maximum depth (null for all), excludes self (depth=0)
     * @return list of descendants ordered by depth ascending
     */
    List<PersonTree> findDescendants(Long personId, Integer maxDepth);

    /**
     * Find the specified number of generations of ancestors.
     *
     * @param personId the person ID
     * @param generations number of generations to retrieve
     * @return list of PersonTree entries for the specified generations
     */
    List<PersonTree> findAncestorsGenerations(Long personId, int generations);

    /**
     * Find the specified number of generations of descendants.
     *
     * @param personId the person ID
     * @param generations number of generations to retrieve
     * @return list of PersonTree entries for the specified generations
     */
    List<PersonTree> findDescendantsGenerations(Long personId, int generations);

    /**
     * Find a specific tree relationship.
     */
    Optional<PersonTree> findByAncestorAndDescendant(Long ancestorId, Long descendantId);

    /**
     * Delete a tree relationship.
     */
    void delete(Long ancestorId, Long descendantId);

    /**
     * Delete all tree relationships for regeneration after relationship changes.
     * Should only be used in special scenarios (usually for testing/maintenance).
     */
    void deleteAll();
}

