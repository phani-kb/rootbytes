/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Flag;
import com.github.phanikb.rootbytes.enums.FlagStatus;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {

    List<Flag> findByRecipeId(UUID recipeId);

    Page<Flag> findByStatus(FlagStatus status, Pageable pageable);

    @Query("SELECT f FROM Flag f WHERE f.status = 'PENDING' ORDER BY f.createdAt ASC")
    Page<Flag> findPendingFlags(Pageable pageable);

    long countByRecipeIdAndStatus(UUID recipeId, FlagStatus status);

    boolean existsByRecipeIdAndUserId(UUID recipeId, UUID userId);

    Page<Flag> findByUserId(UUID userId, Pageable pageable);
}
