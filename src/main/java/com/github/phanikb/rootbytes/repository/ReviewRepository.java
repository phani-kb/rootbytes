/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.Review;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByRecipeAndReviewer(Recipe recipe, UserEntity reviewer);

    List<Review> findByRecipe(Recipe recipe);

    List<Review> findByRecipeAndStatus(Recipe recipe, ModerationStatus status);

    long countByRecipeAndStatus(Recipe recipe, ModerationStatus status);

    boolean existsByRecipeAndReviewer(Recipe recipe, UserEntity reviewer);
}
