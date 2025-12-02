/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Instruction;
import com.github.phanikb.rootbytes.entity.Recipe;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, UUID> {

    List<Instruction> findByRecipeOrderByStepNumberAsc(Recipe recipe);

    void deleteByRecipe(Recipe recipe);

    void deleteByRecipeId(UUID recipeId);

    @Query("SELECT i FROM Instruction i WHERE i.recipe.id = :recipeId ORDER BY i.stepNumber ASC")
    List<Instruction> findByRecipeIdOrderByStepNumber(@Param("recipeId") UUID recipeId);
}
