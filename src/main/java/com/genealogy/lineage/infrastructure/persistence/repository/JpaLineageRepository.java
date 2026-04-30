package com.genealogy.lineage.infrastructure.persistence.repository;

import com.genealogy.lineage.infrastructure.persistence.entity.LineageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineageRepository extends JpaRepository<LineageEntity, Long> {}
