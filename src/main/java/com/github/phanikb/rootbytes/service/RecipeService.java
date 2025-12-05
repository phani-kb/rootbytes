/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.IngredientRequest;
import com.github.phanikb.rootbytes.dto.v1.request.InstructionRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeDietaryInfoRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeRequest;
import com.github.phanikb.rootbytes.entity.Ingredient;
import com.github.phanikb.rootbytes.entity.Instruction;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.RecipeDietaryInfo;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.RecipeStatus;
import com.github.phanikb.rootbytes.exception.RecipeNotFoundException;
import com.github.phanikb.rootbytes.exception.UnauthorizedAccessException;
import com.github.phanikb.rootbytes.mapper.IngredientMapper;
import com.github.phanikb.rootbytes.mapper.InstructionMapper;
import com.github.phanikb.rootbytes.repository.RecipeRepository;
import com.github.phanikb.rootbytes.repository.UnitRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientMapper ingredientMapper;
    private final InstructionMapper instructionMapper;
    private final UnitRepository unitRepository;

    @Transactional(readOnly = true)
    public Recipe getRecipeById(UUID id) {
        log.debug("Fetching recipe with id: {}", id);
        return recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Recipe> getPublishedRecipes(UserEntity user, Pageable pageable) {
        log.debug("Fetching published recipes");
        return recipeRepository.findPublishedRecipes(RecipeStatus.PUBLISHED, user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Recipe> getRecipesByLastName(String lastName, Pageable pageable) {
        log.debug("Fetching recipes for lastName: {}", lastName);
        return recipeRepository.findByLastName(lastName, pageable);
    }

    @Transactional
    public Recipe createRecipe(RecipeRequest request, UserEntity author) {
        log.info("Creating recipe '{}' for author {}", request.getTitle(), author.getId());

        Recipe recipe = Recipe.builder()
                .author(author)
                .title(request.getTitle())
                .description(request.getDescription())
                .story(request.getStory())
                .version(1)
                .status(RecipeStatus.DRAFT)
                .isCurrentVersion(true)
                .isPrivate(Boolean.TRUE.equals(request.getIsPrivate()))
                .prepTimeMinutes(request.getPrepTimeMinutes())
                .cookTimeMinutes(request.getCookTimeMinutes())
                .servings(request.getServings())
                .difficulty(request.getDifficulty())
                .cuisine(request.getCuisine())
                .category(request.getCategory())
                .build();

        addDietaryInfoToRecipe(recipe, request.getDietaryInfo());
        addInstructionsToRecipe(recipe, request.getInstructions());
        addIngredientsToRecipe(recipe, request.getIngredients());
        return recipeRepository.save(recipe);
    }

    private void addDietaryInfoToRecipe(Recipe recipe, RecipeDietaryInfoRequest dietaryRequest) {
        RecipeDietaryInfo.RecipeDietaryInfoBuilder builder =
                RecipeDietaryInfo.builder().recipe(recipe);

        if (dietaryRequest != null) {
            builder.isVegetarian(dietaryRequest.getIsVegetarian() == null || dietaryRequest.getIsVegetarian())
                    .isVegan(Boolean.TRUE.equals(dietaryRequest.getIsVegan()))
                    .isDairyFree(Boolean.TRUE.equals(dietaryRequest.getIsDairyFree()))
                    .isGlutenFree(Boolean.TRUE.equals(dietaryRequest.getIsGlutenFree()))
                    .hasNuts(Boolean.TRUE.equals(dietaryRequest.getHasNuts()))
                    .hasOnion(Boolean.TRUE.equals(dietaryRequest.getHasOnion()))
                    .hasGarlic(Boolean.TRUE.equals(dietaryRequest.getHasGarlic()))
                    .hasEggs(Boolean.TRUE.equals(dietaryRequest.getHasEggs()))
                    .hasSoy(Boolean.TRUE.equals(dietaryRequest.getHasSoy()))
                    .hasShellfish(Boolean.TRUE.equals(dietaryRequest.getHasShellfish()));
        }

        recipe.setDietaryInfo(builder.build());
    }

    private void addIngredientsToRecipe(Recipe recipe, List<IngredientRequest> ingredientRequests) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientRequests != null) {
            for (IngredientRequest req : ingredientRequests) {
                Ingredient ingredient = ingredientMapper.toEntity(req);
                ingredient.setRecipe(recipe);
                if (req.getUnitId() != null) {
                    UUID unitId = req.getUnitId();
                    unitRepository.findById(unitId).ifPresent(ingredient::setUnit);
                }
                ingredients.add(ingredient);
            }
        }
        recipe.setIngredients(ingredients);
    }

    private void addInstructionsToRecipe(Recipe recipe, List<InstructionRequest> instructions) {
        List<Instruction> instructionList = new ArrayList<>();
        if (instructions != null) {
            for (InstructionRequest req : instructions) {
                Instruction instruction = instructionMapper.toEntity(req);
                instruction.setRecipe(recipe);
                instructionList.add(instruction);
            }
        }
        recipe.setInstructions(instructionList);
    }

    @Transactional
    public Recipe updateRecipe(UUID id, RecipeRequest request, UserEntity user) {
        log.info("Updating recipe: {}", id);
        Recipe recipe = getRecipeById(id);

        if (!recipe.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("recipe", "update");
        }

        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        addIngredientsToRecipe(recipe, request.getIngredients());
        addInstructionsToRecipe(recipe, request.getInstructions());
        return recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(UUID id, UserEntity user) {
        log.info("Deleting recipe: {}", id);
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(id));

        if (!recipe.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("recipe", "delete");
        }

        recipeRepository.delete(recipe);
        log.info("Recipe {} deleted by user {}", id, user.getId());
    }
}
