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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.RecipeImage;
import com.github.phanikb.rootbytes.enums.recipe.RecipeImageApprovalStatus;

@Repository
public interface RecipeImageRepository extends JpaRepository<RecipeImage, UUID> {

    long countByRecipeId(UUID recipeId);

    long countByRecipeIdAndApprovalStatus(UUID recipeId, RecipeImageApprovalStatus status);

    List<RecipeImage> findByRecipeIdOrderByOrderIndexAsc(UUID recipeId);

    @EntityGraph(attributePaths = {"uploadedBy"})
    List<RecipeImage> findByRecipeIdAndApprovalStatusOrderByOrderIndexAsc(
            UUID recipeId, RecipeImageApprovalStatus status);

    @EntityGraph(attributePaths = {"uploadedBy"})
    List<RecipeImage> findByRecipeIdAndApprovalStatusOrderByUploadedAtDesc(
            UUID recipeId, RecipeImageApprovalStatus status);

    @EntityGraph(attributePaths = {"recipe", "recipe.author", "uploadedBy"})
    Page<RecipeImage> findByApprovalStatusOrderByUploadedAtAsc(RecipeImageApprovalStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"recipe"})
    Page<RecipeImage> findByUploadedByIdOrderByUploadedAtDesc(UUID uploaderId, Pageable pageable);

    @EntityGraph(attributePaths = {"recipe", "recipe.author", "uploadedBy"})
    Optional<RecipeImage> findById(UUID imageId);

    Optional<RecipeImage> findByRecipeIdAndIsPrimaryTrueAndApprovalStatus(
            UUID recipeId, RecipeImageApprovalStatus status);

    Optional<RecipeImage> findTopByRecipeIdOrderByOrderIndexDesc(UUID recipeId);

    @Modifying
    @Query("UPDATE RecipeImage ri SET ri.isPrimary = :isPrimary WHERE ri.recipe.id = :recipeId")
    void updatePrimaryFlag(@Param("recipeId") UUID recipeId, @Param("isPrimary") boolean isPrimary);

    @Modifying
    @Query("UPDATE RecipeImage ri SET ri.isPrimary = true WHERE ri.id = :imageId")
    void setPrimaryImage(@Param("imageId") UUID imageId);
}
