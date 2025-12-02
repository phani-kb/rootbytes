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

import static com.github.phanikb.rootbytes.common.ValidationConstants.MAX_INGREDIENTS;
import static com.github.phanikb.rootbytes.common.ValidationConstants.MAX_INSTRUCTIONS;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.CATEGORY_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MUST_BE_POSITIVE_OR_ZERO;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TEXT_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TITLE_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TITLE_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_S;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    @NotBlank(message = TITLE_REQUIRED)
    @Size(max = SIZE_M, message = TITLE_TOO_LONG)
    private String title;

    @Size(max = SIZE_XL, message = TEXT_TOO_LONG)
    private String description;

    @Size(max = SIZE_XL, message = TEXT_TOO_LONG)
    private String story;

    @Valid
    @Size(max = MAX_INGREDIENTS, message = "Maximum " + MAX_INGREDIENTS + " ingredients allowed")
    private List<IngredientRequest> ingredients;

    @Valid
    @Size(max = MAX_INSTRUCTIONS, message = "Maximum " + MAX_INSTRUCTIONS + " instructions allowed")
    private List<InstructionRequest> instructions;

    @PositiveOrZero(message = "Prep time" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer prepTimeMinutes;

    @PositiveOrZero(message = "Cook time" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer cookTimeMinutes;

    @PositiveOrZero(message = "Servings" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer servings;

    private RecipeDifficulty difficulty;

    @Size(max = SIZE_S, message = CATEGORY_TOO_LONG)
    private String cuisine;

    @Size(max = SIZE_S, message = CATEGORY_TOO_LONG)
    private String category;

    private Boolean isPrivate;

    @Valid
    private RecipeDietaryInfoRequest dietaryInfo;
}
