/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.RecipeDifficulty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 65_535, message = "Description is too long")
    private String description;

    @Size(max = 65_535, message = "Story is too long")
    private String story;

    @Valid
    private List<IngredientRequest> ingredients;

    @Valid
    private List<InstructionRequest> instructions;

    @PositiveOrZero(message = "Prep time must be positive")
    private Integer prepTimeMinutes;

    @PositiveOrZero(message = "Cook time must be positive")
    private Integer cookTimeMinutes;

    @PositiveOrZero(message = "Servings must be positive")
    private Integer servings;

    private RecipeDifficulty difficulty;

    @Size(max = 50, message = "Cuisine must not exceed 50 characters")
    private String cuisine;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    private Boolean isPrivate;

    @Valid
    private RecipeDietaryInfoRequest dietaryInfo;
}
