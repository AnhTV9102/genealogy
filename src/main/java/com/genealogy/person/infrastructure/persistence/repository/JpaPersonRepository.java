package com.genealogy.person.infrastructure.persistence.repository;

import com.genealogy.person.infrastructure.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPersonRepository extends JpaRepository<PersonEntity, Long> {}
