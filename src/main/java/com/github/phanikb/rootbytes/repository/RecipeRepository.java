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

import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.RecipeStatus;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    Optional<Recipe> findByAuthorAndTitleAndIsCurrentVersionTrue(UserEntity author, String title);

    List<Recipe> findByAuthorAndIsCurrentVersionTrue(UserEntity author);

    Page<Recipe> findByStatusAndIsCurrentVersionTrue(RecipeStatus status, Pageable pageable);

    @Query("""
            SELECT r FROM Recipe r
            WHERE r.status = :status
            AND r.isCurrentVersion = true
            AND (r.isPrivate = false OR r.author = :user)
            """)
    Page<Recipe> findPublishedRecipes(
            @Param("status") RecipeStatus status, @Param("user") UserEntity user, Pageable pageable);

    @Query("""
            SELECT r FROM Recipe r
            WHERE r.author.lastName = :lastName
            AND r.status = 'PUBLISHED'
            AND r.isCurrentVersion = true
            AND r.isPrivate = false
            """)
    Page<Recipe> findByLastName(@Param("lastName") String lastName, Pageable pageable);

    List<Recipe> findByAuthorId(UUID authorId);

    Page<Recipe> findByStatus(RecipeStatus status, Pageable pageable);
}
