/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.phanikb.rootbytes.dto.v1.request.IngredientRequest;
import com.github.phanikb.rootbytes.dto.v1.request.InstructionRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeDietaryInfoRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeRequest;
import com.github.phanikb.rootbytes.entity.Ingredient;
import com.github.phanikb.rootbytes.entity.Instruction;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.RecipeDifficulty;
import com.github.phanikb.rootbytes.enums.RecipeStatus;
import com.github.phanikb.rootbytes.exception.RecipeNotFoundException;
import com.github.phanikb.rootbytes.exception.UnauthorizedAccessException;
import com.github.phanikb.rootbytes.mapper.IngredientMapper;
import com.github.phanikb.rootbytes.mapper.InstructionMapper;
import com.github.phanikb.rootbytes.repository.RecipeRepository;
import com.github.phanikb.rootbytes.repository.UnitRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private InstructionMapper instructionMapper;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    private Validator validator;

    private UUID recipeId;
    private UUID authorId;
    private UUID unitId;
    private UserEntity author;
    private Recipe recipe;
    private Unit unit;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        recipeId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        unitId = UUID.randomUUID();

        author = UserEntity.builder()
                .id(authorId)
                .email("chef@example.com")
                .lastName("Chef")
                .uniqueName("CHEF01")
                .build();

        unit = Unit.builder().id(unitId).name("Cup").abbreviation("cup").build();

        recipe = Recipe.builder()
                .id(recipeId)
                .author(author)
                .title("Test Recipe")
                .description("A delicious test recipe")
                .story("This recipe has a story")
                .version(1)
                .status(RecipeStatus.DRAFT)
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
    }

    @Test
    void shouldGetRecipeById() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getRecipeById(recipeId);

        assertNotNull(result);
        assertEquals(recipeId, result.getId());
        assertEquals("Test Recipe", result.getTitle());
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void shouldThrowExceptionWhenRecipeNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(recipeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(nonExistentId));
        verify(recipeRepository).findById(nonExistentId);
    }

    @Test
    void shouldGetPublishedRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));
        when(recipeRepository.findPublishedRecipes(RecipeStatus.PUBLISHED, author, pageable))
                .thenReturn(recipePage);

        Page<Recipe> result = recipeService.getPublishedRecipes(author, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findPublishedRecipes(RecipeStatus.PUBLISHED, author, pageable);
    }

    @Test
    void shouldGetRecipesByLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));
        when(recipeRepository.findByLastName("Chef", pageable)).thenReturn(recipePage);

        Page<Recipe> result = recipeService.getRecipesByLastName("Chef", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByLastName("Chef", pageable);
    }

    @Test
    void shouldCreateRecipeWithMinimalData() {
        RecipeRequest request = RecipeRequest.builder()
                .title("Simple Recipe")
                .description("A simple recipe")
                .build();

        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        Recipe result = recipeService.createRecipe(request, author);

        assertNotNull(result);
        verify(recipeRepository).save(recipeCaptor.capture());

        Recipe captured = recipeCaptor.getValue();
        assertEquals("Simple Recipe", captured.getTitle());
        assertEquals(author, captured.getAuthor());
        assertEquals(RecipeStatus.DRAFT, captured.getStatus());
        assertTrue(captured.getIsCurrentVersion());
        assertFalse(captured.getIsPrivate());
        assertNotNull(captured.getDietaryInfo());
        assertFalse(captured.getDietaryInfo().getIsVegetarian());
    }

    @Test
    void shouldCreateRecipeWithFullDetails() {
        RecipeDietaryInfoRequest dietaryInfo = RecipeDietaryInfoRequest.builder()
                .isVegetarian(false)
                .isVegan(false)
                .isDairyFree(true)
                .isGlutenFree(true)
                .hasNuts(true)
                .hasOnion(true)
                .hasGarlic(true)
                .build();

        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("Flour")
                .quantity(2.0)
                .unitId(unitId)
                .orderIndex(1)
                .build();

        InstructionRequest instructionRequest = InstructionRequest.builder()
                .description("Mix all ingredients")
                .durationMinutes(5)
                .build();

        RecipeRequest request = RecipeRequest.builder()
                .title("Full Recipe")
                .description("A complete recipe")
                .story("The story behind this recipe")
                .prepTimeMinutes(20)
                .cookTimeMinutes(45)
                .servings(6)
                .difficulty(RecipeDifficulty.HARD)
                .cuisine("French")
                .category("Dessert")
                .isPrivate(true)
                .dietaryInfo(dietaryInfo)
                .ingredients(List.of(ingredientRequest))
                .instructions(List.of(instructionRequest))
                .build();

        Ingredient ingredient = Ingredient.builder().name("Flour").build();

        Instruction instruction = Instruction.builder()
                .description("Mix all ingredients")
                .durationMinutes(5)
                .build();

        when(ingredientMapper.toEntity(any(IngredientRequest.class))).thenReturn(ingredient);
        when(instructionMapper.toEntity(any(InstructionRequest.class))).thenReturn(instruction);
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        Recipe result = recipeService.createRecipe(request, author);

        assertNotNull(result);
        verify(recipeRepository).save(recipeCaptor.capture());

        Recipe captured = recipeCaptor.getValue();
        assertEquals("Full Recipe", captured.getTitle());
        assertEquals("The story behind this recipe", captured.getStory());
        assertEquals(20, captured.getPrepTimeMinutes());
        assertEquals(45, captured.getCookTimeMinutes());
        assertEquals(6, captured.getServings());
        assertEquals(RecipeDifficulty.HARD, captured.getDifficulty());
        assertEquals("French", captured.getCuisine());
        assertEquals("Dessert", captured.getCategory());
        assertTrue(captured.getIsPrivate());

        assertNotNull(captured.getDietaryInfo());
        assertFalse(captured.getDietaryInfo().getIsVegetarian());
        assertTrue(captured.getDietaryInfo().getIsDairyFree());
        assertTrue(captured.getDietaryInfo().getIsGlutenFree());
        assertTrue(captured.getDietaryInfo().getHasNuts());
    }

    @Test
    void shouldCreateRecipeWithNullDietaryInfoUsingDefaults() {
        RecipeRequest request = RecipeRequest.builder()
                .title("Recipe Without Dietary Info")
                .description("No dietary info provided")
                .dietaryInfo(null)
                .build();

        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        recipeService.createRecipe(request, author);

        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe captured = recipeCaptor.getValue();

        assertNotNull(captured.getDietaryInfo());
        assertFalse(captured.getDietaryInfo().getIsVegetarian());
        assertFalse(captured.getDietaryInfo().getIsVegan());
        assertFalse(captured.getDietaryInfo().getIsDairyFree());
        assertFalse(captured.getDietaryInfo().getHasNuts());
    }

    @Test
    void shouldUpdateRecipeByAuthor() {
        RecipeRequest updateRequest = RecipeRequest.builder()
                .title("Updated Recipe Title")
                .description("Updated description")
                .build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe result = recipeService.updateRecipe(recipeId, updateRequest, author);

        assertNotNull(result);
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void shouldThrowExceptionWhenNonAuthorUpdates() {
        UserEntity otherUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("other@example.com")
                .lastName("Other")
                .uniqueName("OTHER1")
                .build();

        RecipeRequest updateRequest =
                RecipeRequest.builder().title("Hacked Title").build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(
                UnauthorizedAccessException.class,
                () -> recipeService.updateRecipe(recipeId, updateRequest, otherUser));

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void shouldDeleteRecipeWhenUserIsAuthor() {
        recipe.setAuthor(author);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(recipeId, author);

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRecipe() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe(recipeId, author));

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void shouldThrowExceptionWhenNonAuthorTriesToDelete() {
        UserEntity otherUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("other@example.com")
                .lastName("Other")
                .uniqueName("OTHER1")
                .build();

        recipe.setAuthor(author);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(UnauthorizedAccessException.class, () -> recipeService.deleteRecipe(recipeId, otherUser));

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void shouldHandleEmptyIngredientsAndInstructions() {
        RecipeRequest request = RecipeRequest.builder()
                .title("Recipe Without Lists")
                .description("No ingredients or instructions")
                .ingredients(null)
                .instructions(null)
                .build();

        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        Recipe result = recipeService.createRecipe(request, author);

        assertNotNull(result);
        verify(ingredientMapper, never()).toEntity(any());
        verify(instructionMapper, never()).toEntity(any());
    }

    @Test
    void shouldNotLookupUnitWhenUnitIdIsNull() {
        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("Salt")
                .quantity(1.0)
                .unitId(null) // no unit
                .orderIndex(1)
                .build();

        RecipeRequest request = RecipeRequest.builder()
                .title("Recipe With Unitless Ingredient")
                .ingredients(List.of(ingredientRequest))
                .build();

        Ingredient ingredient = Ingredient.builder().name("Salt").build();

        when(ingredientMapper.toEntity(any(IngredientRequest.class))).thenReturn(ingredient);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        recipeService.createRecipe(request, author);

        verify(unitRepository, never()).findById(any());
    }

    @Test
    void shouldRejectIngredientWithoutUnit() {
        IngredientRequest request =
                IngredientRequest.builder().name("Salt").unitId(null).build();
        Set<ConstraintViolation<IngredientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}
