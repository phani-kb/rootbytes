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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.RecipeImageModeration;
import com.github.phanikb.rootbytes.enums.ModerationAction;

@Repository
public interface RecipeImageModerationRepository extends JpaRepository<RecipeImageModeration, UUID> {

    @EntityGraph(attributePaths = {"image", "moderator"})
    List<RecipeImageModeration> findByImageIdOrderByCreatedAtDesc(UUID imageId);

    @EntityGraph(attributePaths = {"image", "moderator"})
    Page<RecipeImageModeration> findByModeratorIdOrderByCreatedAtDesc(UUID moderatorId, Pageable pageable);

    @EntityGraph(attributePaths = {"image", "moderator"})
    Page<RecipeImageModeration> findByActionOrderByCreatedAtDesc(ModerationAction action, Pageable pageable);

    long countByImageId(UUID imageId);

    RecipeImageModeration findFirstByImageIdOrderByCreatedAtDesc(UUID imageId);
}
