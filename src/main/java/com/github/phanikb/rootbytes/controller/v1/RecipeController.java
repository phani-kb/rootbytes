/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.RecipeRequest;
import com.github.phanikb.rootbytes.dto.v1.response.PagedResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeDetailResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeResponse;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.RecipeMapper;
import com.github.phanikb.rootbytes.security.RbCurrentUser;
import com.github.phanikb.rootbytes.service.RecipeService;

@Slf4j
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @GetMapping
    public ResponseEntity<RbApiResponse<PagedResponse<RecipeResponse>>> getAllRecipes(
            @RbCurrentUser UserEntity user, Pageable pageable) {
        log.debug("Fetching all recipes");

        Page<Recipe> recipes = recipeService.getPublishedRecipes(user, pageable);
        Page<RecipeResponse> responsePage = recipes.map(recipeMapper::toResponse);

        PagedResponse<RecipeResponse> pagedResponse = PagedResponse.of(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages());

        return ResponseEntity.ok(RbApiResponse.success("Recipes retrieved successfully", pagedResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RbApiResponse<RecipeDetailResponse>> getRecipeById(@PathVariable UUID id) {
        log.debug("Fetching recipe with id: {}", id);

        Recipe recipe = recipeService.getRecipeById(id);
        RecipeDetailResponse response = recipeMapper.toDetailResponse(recipe);

        return ResponseEntity.ok(RbApiResponse.success("Recipe retrieved successfully", response));
    }

    @PostMapping
    public ResponseEntity<RbApiResponse<RecipeResponse>> createRecipe(
            @Valid @RequestBody RecipeRequest request, @RbCurrentUser UserEntity user) {
        log.info("Creating new recipe");

        Recipe recipe = recipeService.createRecipe(request, user);
        RecipeResponse response = recipeMapper.toResponse(recipe);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Recipe created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RbApiResponse<RecipeResponse>> updateRecipe(
            @PathVariable UUID id, @Valid @RequestBody RecipeRequest request, @RbCurrentUser UserEntity user) {
        log.info("Updating recipe with id: {}", id);

        Recipe recipe = recipeService.updateRecipe(id, request, user);
        RecipeResponse response = recipeMapper.toResponse(recipe);

        return ResponseEntity.ok(RbApiResponse.success("Recipe updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RbApiResponse<Void>> deleteRecipe(@PathVariable UUID id, @RbCurrentUser UserEntity user) {
        log.info("Deleting recipe with id: {}", id);

        recipeService.deleteRecipe(id, user);

        return ResponseEntity.ok(RbApiResponse.success("Recipe deleted successfully"));
    }
}
