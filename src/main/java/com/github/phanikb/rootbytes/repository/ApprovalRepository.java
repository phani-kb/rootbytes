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

import com.github.phanikb.rootbytes.entity.Approval;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, UUID> {

    Optional<Approval> findByRecipeAndApprover(Recipe recipe, UserEntity approver);

    List<Approval> findByRecipe(Recipe recipe);

    List<Approval> findByRecipeAndStatus(Recipe recipe, ModerationStatus status);

    long countByRecipeAndStatus(Recipe recipe, ModerationStatus status);

    boolean existsByRecipeAndApprover(Recipe recipe, UserEntity approver);
}
