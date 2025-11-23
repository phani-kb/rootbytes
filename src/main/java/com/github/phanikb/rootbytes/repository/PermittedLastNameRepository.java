/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.PermittedLastName;

@Repository
public interface PermittedLastNameRepository extends JpaRepository<PermittedLastName, UUID> {

    Optional<PermittedLastName> findByLastName(String lastName);

    Optional<PermittedLastName> findByLastNameIgnoreCase(String lastName);

    boolean existsByLastName(String lastName);

    boolean existsByLastNameIgnoreCase(String lastName);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END "
            + "FROM PermittedLastName p WHERE LOWER(p.lastName) = LOWER(:lastName) AND p.isActive = true")
    boolean existsActiveByLastNameIgnoreCase(@Param("lastName") String lastName);

    List<PermittedLastName> findByIsActiveTrue();

    Page<PermittedLastName> findByIsActiveTrue(Pageable pageable);

    List<PermittedLastName> findByCategory(String category);

    List<PermittedLastName> findByCategoryAndIsActiveTrue(String category);

    @Query("SELECT p FROM PermittedLastName p WHERE LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PermittedLastName> searchByLastName(@Param("searchTerm") String searchTerm);

    @Query(
            "SELECT DISTINCT p FROM PermittedLastName p LEFT JOIN p.aliases a WHERE (LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(a.alias) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND p.isActive = true")
    List<PermittedLastName> searchActiveByLastName(@Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT p.category FROM PermittedLastName p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    long countByCategory(String category);

    long countByIsActiveTrue();
}
