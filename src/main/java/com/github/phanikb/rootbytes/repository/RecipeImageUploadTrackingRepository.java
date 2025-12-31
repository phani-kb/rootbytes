/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.RecipeImageUploadTracking;

@Repository
public interface RecipeImageUploadTrackingRepository extends JpaRepository<RecipeImageUploadTracking, UUID> {

    Optional<RecipeImageUploadTracking> findByRecipeIdAndUserId(UUID recipeId, UUID userId);

    @Modifying
    @Query("UPDATE RecipeImageUploadTracking t SET t.pendingCount = t.pendingCount + 1, "
            + "t.totalUploads = t.totalUploads + 1, t.lastUploadAt = :lastUploadAt "
            + "WHERE t.recipe.id = :recipeId AND t.user.id = :userId")
    int incrementPendingCount(
            @Param("recipeId") UUID recipeId,
            @Param("userId") UUID userId,
            @Param("lastUploadAt") Instant lastUploadAt);

    @Modifying
    @Query("UPDATE RecipeImageUploadTracking t SET t.pendingCount = t.pendingCount - 1, "
            + "t.approvedCount = t.approvedCount + 1 "
            + "WHERE t.recipe.id = :recipeId AND t.user.id = :userId AND t.pendingCount > 0")
    int moveToApproved(@Param("recipeId") UUID recipeId, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE RecipeImageUploadTracking t SET t.pendingCount = t.pendingCount - 1, "
            + "t.rejectedCount = t.rejectedCount + 1 "
            + "WHERE t.recipe.id = :recipeId AND t.user.id = :userId AND t.pendingCount > 0")
    int moveToRejected(@Param("recipeId") UUID recipeId, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE RecipeImageUploadTracking t SET t.approvedCount = t.approvedCount - 1 "
            + "WHERE t.recipe.id = :recipeId AND t.user.id = :userId AND t.approvedCount > 0")
    int decrementApprovedCount(@Param("recipeId") UUID recipeId, @Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(t.totalUploads), 0) FROM RecipeImageUploadTracking t WHERE t.recipe.id = :recipeId")
    long getTotalUploadsForRecipe(@Param("recipeId") UUID recipeId);

    @Query("SELECT COALESCE(SUM(t.approvedCount), 0) FROM RecipeImageUploadTracking t WHERE t.recipe.id = :recipeId")
    long getTotalApprovedForRecipe(@Param("recipeId") UUID recipeId);
}
