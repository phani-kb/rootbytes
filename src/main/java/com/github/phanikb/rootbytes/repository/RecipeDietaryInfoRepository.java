/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.RecipeDietaryInfo;

@Repository
public interface RecipeDietaryInfoRepository extends JpaRepository<RecipeDietaryInfo, UUID> {

    Optional<RecipeDietaryInfo> findByRecipeId(UUID recipeId);
}
