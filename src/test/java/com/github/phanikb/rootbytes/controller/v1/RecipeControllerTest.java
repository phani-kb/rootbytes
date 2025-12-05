/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.github.phanikb.rootbytes.dto.v1.request.RecipeDietaryInfoRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeRequest;
import com.github.phanikb.rootbytes.dto.v1.response.PagedResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeDetailResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeDietaryInfoResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeResponse;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.RecipeDifficulty;
import com.github.phanikb.rootbytes.enums.RecipeStatus;
import com.github.phanikb.rootbytes.mapper.RecipeMapper;
import com.github.phanikb.rootbytes.service.RecipeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @Mock
    private RecipeMapper recipeMapper;

    private RecipeController controller;

    private UUID recipeId;
    private UUID authorId;
    private UserEntity author;
    private Recipe recipe;
    private RecipeResponse recipeResponse;
    private RecipeDetailResponse recipeDetailResponse;

    @BeforeEach
    void setUp() {
        controller = new RecipeController(recipeService, recipeMapper);

        recipeId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        author = UserEntity.builder()
                .id(authorId)
                .email("chef@example.com")
                .lastName("Chef")
                .uniqueName("CHEF01")
                .build();

        recipe = Recipe.builder()
                .id(recipeId)
                .author(author)
                .title("Test Recipe")
                .description("A test recipe description")
                .version(1)
                .status(RecipeStatus.PUBLISHED)
                .isCurrentVersion(true)
                .isPrivate(false)
                .prepTimeMinutes(15)
                .cookTimeMinutes(30)
                .servings(4)
                .difficulty(RecipeDifficulty.MEDIUM)
                .cuisine("Italian")
                .category("Main Course")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        recipeResponse = RecipeResponse.builder()
                .id(recipeId)
                .authorId(authorId)
                .title("Test Recipe")
                .description("A test recipe description")
                .version(1)
                .status("PUBLISHED")
                .isCurrentVersion(true)
                .isPrivate(false)
                .prepTimeMinutes(15)
                .cookTimeMinutes(30)
                .servings(4)
                .difficulty("MEDIUM")
                .cuisine("Italian")
                .category("Main Course")
                .build();

        RecipeDietaryInfoResponse dietaryInfoResponse = RecipeDietaryInfoResponse.builder()
                .isVegetarian(true)
                .isVegan(false)
                .isDairyFree(false)
                .isGlutenFree(false)
                .hasNuts(false)
                .hasOnion(true)
                .hasGarlic(true)
                .build();

        recipeDetailResponse = RecipeDetailResponse.builder()
                .id(recipeId)
                .authorId(authorId)
                .authorName("Test Chef")
                .title("Test Recipe")
                .description("A test recipe description")
                .version(1)
                .status("PUBLISHED")
                .isCurrentVersion(true)
                .isPrivate(false)
                .dietaryInfo(dietaryInfoResponse)
                .prepTimeMinutes(15)
                .cookTimeMinutes(30)
                .servings(4)
                .difficulty("MEDIUM")
                .cuisine("Italian")
                .category("Main Course")
                .build();
    }

    @Test
    void shouldGetAllRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe), pageable, 1);

        when(recipeService.getPublishedRecipes(any(UserEntity.class), eq(pageable)))
                .thenReturn(recipePage);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(recipeResponse);

        var result = controller.getAllRecipes(author, pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Recipes retrieved successfully", result.getBody().getMessage());

        PagedResponse<RecipeResponse> pagedResponse = result.getBody().getData();
        assertNotNull(pagedResponse);
        assertEquals(1, pagedResponse.getContent().size());
        assertEquals(0, pagedResponse.getPage());
        assertEquals(10, pagedResponse.getSize());
        assertEquals(1, pagedResponse.getTotalElements());

        verify(recipeService).getPublishedRecipes(any(UserEntity.class), eq(pageable));
    }

    @Test
    void shouldGetRecipeById() {
        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
        when(recipeMapper.toDetailResponse(any(Recipe.class))).thenReturn(recipeDetailResponse);

        var result = controller.getRecipeById(recipeId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Recipe retrieved successfully", result.getBody().getMessage());

        RecipeDetailResponse response = result.getBody().getData();
        assertNotNull(response);
        assertEquals(recipeId, response.getId());
        assertEquals("Test Recipe", response.getTitle());
        assertNotNull(response.getDietaryInfo());
        assertTrue(response.getDietaryInfo().getIsVegetarian());

        verify(recipeService).getRecipeById(recipeId);
        verify(recipeMapper).toDetailResponse(any(Recipe.class));
    }

    @Test
    void shouldCreateRecipe() {
        RecipeDietaryInfoRequest dietaryInfoRequest = RecipeDietaryInfoRequest.builder()
                .isVegetarian(true)
                .hasOnion(true)
                .hasGarlic(true)
                .build();

        RecipeRequest request = RecipeRequest.builder()
                .title("New Recipe")
                .description("A brand new recipe")
                .prepTimeMinutes(20)
                .cookTimeMinutes(40)
                .servings(4)
                .difficulty(RecipeDifficulty.EASY)
                .cuisine("Mexican")
                .category("Appetizer")
                .dietaryInfo(dietaryInfoRequest)
                .build();

        when(recipeService.createRecipe(any(RecipeRequest.class), any(UserEntity.class)))
                .thenReturn(recipe);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(recipeResponse);

        var result = controller.createRecipe(request, author);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Recipe created successfully", result.getBody().getMessage());

        RecipeResponse response = result.getBody().getData();
        assertNotNull(response);
        assertEquals(recipeId, response.getId());

        verify(recipeService).createRecipe(any(RecipeRequest.class), any(UserEntity.class));
        verify(recipeMapper).toResponse(any(Recipe.class));
    }

    @Test
    void shouldUpdateRecipe() {
        RecipeRequest request = RecipeRequest.builder()
                .title("Updated Recipe")
                .description("Updated description")
                .build();

        when(recipeService.updateRecipe(eq(recipeId), any(RecipeRequest.class), any(UserEntity.class)))
                .thenReturn(recipe);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(recipeResponse);

        var result = controller.updateRecipe(recipeId, request, author);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Recipe updated successfully", result.getBody().getMessage());

        verify(recipeService).updateRecipe(eq(recipeId), any(RecipeRequest.class), any(UserEntity.class));
        verify(recipeMapper).toResponse(any(Recipe.class));
    }

    @Test
    void shouldDeleteRecipe() {
        doNothing().when(recipeService).deleteRecipe(eq(recipeId), any(UserEntity.class));

        var result = controller.deleteRecipe(recipeId, author);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Recipe deleted successfully", result.getBody().getMessage());

        verify(recipeService).deleteRecipe(eq(recipeId), any(UserEntity.class));
    }

    @Test
    void shouldReturnEmptyPageWhenNoRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(recipeService.getPublishedRecipes(any(UserEntity.class), eq(pageable)))
                .thenReturn(emptyPage);

        var result = controller.getAllRecipes(author, pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());

        PagedResponse<RecipeResponse> pagedResponse = result.getBody().getData();
        assertNotNull(pagedResponse);
        assertTrue(pagedResponse.getContent().isEmpty());
        assertEquals(0, pagedResponse.getTotalElements());
    }

    @Test
    void shouldHandleMultipleRecipesInPage() {
        Recipe recipe2 = Recipe.builder()
                .id(UUID.randomUUID())
                .author(author)
                .title("Second Recipe")
                .status(RecipeStatus.PUBLISHED)
                .build();

        RecipeResponse response2 = RecipeResponse.builder()
                .id(recipe2.getId())
                .title("Second Recipe")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe, recipe2), pageable, 2);

        when(recipeService.getPublishedRecipes(any(UserEntity.class), eq(pageable)))
                .thenReturn(recipePage);
        when(recipeMapper.toResponse(recipe)).thenReturn(recipeResponse);
        when(recipeMapper.toResponse(recipe2)).thenReturn(response2);

        var result = controller.getAllRecipes(author, pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        PagedResponse<RecipeResponse> pagedResponse = result.getBody().getData();
        assertEquals(2, pagedResponse.getContent().size());
        assertEquals(2, pagedResponse.getTotalElements());
    }
}
